package com.dwolla.codestar.notifications

import cats.effect._
import cats.effect.concurrent.Deferred
import com.dwolla.lambda.NoResponse
import com.dwolla.ses.SesAlg
import io.circe._
import io.circe.literal._
import org.scalatest.matchers.should.Matchers
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class HandlerSpec extends AnyPropSpec with ScalaCheckPropertyChecks with Matchers {
  private implicit val ioContextShift: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)

  private val inputJson: Json =
    json"""{
             "Records": [
               {
                 "EventSource": "aws:sns",
                 "EventVersion": "1.0",
                 "EventSubscriptionArn": "arn:aws:sns:us-west-2:799546647898:terraform-multi-account-codecommit-notifications:c75f95b9-743d-4cda-8270-2c2c4d875ea9",
                 "Sns": {
                   "Type": "Notification",
                   "MessageId": "a04156fd-d451-53a0-93e9-0016d988ad7a",
                   "TopicArn": "arn:aws:sns:us-west-2:799546647898:terraform-multi-account-codecommit-notifications",
                   "Subject": null,
                   "Message": "{\"account\":\"799546647898\",\"detailType\":\"CodeCommit Comment on Pull Request\",\"region\":\"us-west-2\",\"source\":\"aws.codecommit\",\"time\":\"2020-03-17T17:40:38Z\",\"notificationRuleArn\":\"arn:aws:codestar-notifications:us-west-2:799546647898:notificationrule/04424cdb2df02a1a4ed68580d833c3b9c97bd266\",\"detail\":{\"beforeCommitId\":\"700563d87e40715738cadbbd78f5b73f197a1c9f\",\"notificationBody\":\"A pull request event occurred in the following AWS CodeCommit repository: terraform-multi-account. The user: arn:aws:iam::799546647898:user/aquintano made a comment or replied to a comment. The comment was made on the following Pull Request: 92. For more information, go to the AWS CodeCommit console https://us-west-2.console.aws.amazon.com/codesuite/codecommit/repositories/terraform-multi-account/pull-requests/92/activity#c5b1a617-9987-407d-8f5e-34c585359c1c%3A0bd1624c-7c42-46ee-9cc1-10dfab32d47d?region=us-west-2\",\"repositoryId\":\"3af1ac44-a4a5-4549-8fff-c8b91dbbebb1\",\"commentId\":\"c5b1a617-9987-407d-8f5e-34c585359c1c:0bd1624c-7c42-46ee-9cc1-10dfab32d47d\",\"afterCommitId\":\"345b900f3f6c59343d5aaefe679f9b1744f6f4af\",\"callerUserArn\":\"arn:aws:iam::799546647898:user/aquintano\",\"event\":\"commentOnPullRequestCreated\",\"pullRequestId\":\"92\",\"repositoryName\":\"terraform-multi-account\"},\"resources\":[\"arn:aws:codecommit:us-west-2:799546647898:terraform-multi-account\"],\"additionalAttributes\":{}}",
                   "Timestamp": "2020-03-17T17:40:41.378Z",
                   "SignatureVersion": "1",
                   "Signature": "eWNJKkG1xm8CjsCYNjCKLju3OVwlL0WIlGwAK+ljdTq436NcyjibSrt1tSgapSxBbMKEmirqVsfM6eDyJkYrSvOf8QZtsbEG7HqhA31EZXVWT5xIiI0T29mq5WJULhZyX4sz4CO/gStnxdqgJ8R5xuahzVyIRxomAn+4N56ufPhzRDjji0jayh0Zvwftai0yVp6i5Z3k7i2Z+xT58znwKOsbuI8FZuduBIQ6R0Yz6B1UuY7WJ0u5+prOkMU52sh4WbmoSFoMlrKSUrCaWQdfVk0olzZZhmT97tJCcKrurR23oHzXLeUtXGdh7t3jPla01dLv8dWp5cEMgC2gQOK4pg==",
                   "SigningCertUrl": "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-a86cb10b4e1f29c941702d737128f7b6.pem",
                   "UnsubscribeUrl": "https://sns.us-west-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-west-2:799546647898:terraform-multi-account-codecommit-notifications:c75f95b9-743d-4cda-8270-2c2c4d875ea9",
                   "MessageAttributes": {}
                 }
               }
             ]
           }"""


  property("Handler should use the sender and recipient from the environment") {
    forAll { (maybeSender: Option[String], maybeRecipient: Option[String]) =>
      implicit val E: Environment[IO] = TestEnvironment[IO](maybeSender, maybeRecipient)

      val assertions =
        for {
          passedSenderDeferred <- Deferred[IO, String]
          passedRecipientDeferred <- Deferred[IO, String]
          sesAlg = new SesAlg[IO] {
            override def sendEmail(to: String, from: String, subject: String, body: String): IO[Unit] =
              for {
                _ <- passedSenderDeferred.complete(from)
                _ <- passedRecipientDeferred.complete(to)
              } yield ()
          }
          resp <- new Handler(sesAlg).apply(inputJson).attempt
          sender <- passedSenderDeferred.get
          recipient <- passedRecipientDeferred.get
        } yield {
          sender should be(maybeSender.getOrElse("dev-testing@us-west-2.sandbox.dwolla.net"))
          recipient should be(maybeRecipient.getOrElse("dev-testing@dwolla.com"))
          resp should be(Right(NoResponse))
        }

      assertions.unsafeRunSync()
    }
  }

}
