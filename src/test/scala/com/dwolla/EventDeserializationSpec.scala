package com.dwolla

import cats.implicits._
import com.dwolla.codestar.notifications.model.Notification
import com.dwolla.sns.model._
import io.circe._
import io.circe.literal._
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EventDeserializationSpec extends AnyFlatSpec with Matchers with EitherValues {

  val snsPullRequestStateChange =
    json"""{
             "Records": [
               {
                 "EventSource": "aws:sns",
                 "EventVersion": "1.0",
                 "EventSubscriptionArn": "arn:aws:sns:us-west-2:000000000000:CodeCommitPullRequestComments:1f05f39c-05c6-4787-a60b-89c4a4a80bae",
                 "Sns": {
                   "Type": "Notification",
                   "MessageId": "9e19450e-645d-5522-9ed4-1330f679cb70",
                   "TopicArn": "arn:aws:sns:us-west-2:000000000000:CodeCommitPullRequestComments",
                   "Subject": null,
                   "Message": "{\"account\":\"799546647898\",\"detailType\":\"CodeCommit Pull Request State Change\",\"region\":\"us-west-2\",\"source\":\"aws.codecommit\",\"time\":\"2020-03-11T16:37:56Z\",\"notificationRuleArn\":\"arn:aws:codestar-notifications:us-west-2:799546647898:notificationrule/04424cdb2df02a1a4ed68580d833c3b9c97bd266\",\"detail\":{\"sourceReference\":\"refs/heads/add-pr-approval-viewing\",\"lastModifiedDate\":\"Wed Mar 11 16:37:45 UTC 2020\",\"author\":\"arn:aws:iam::799546647898:user/kschlorff\",\"isMerged\":\"False\",\"pullRequestStatus\":\"Open\",\"notificationBody\":\"A pull request event occurred in the following AWS CodeCommit repository: terraform-multi-account. User: arn:aws:iam::799546647898:user/kschlorff. Event: Created. Pull request name: 76. Additional information: A pull request was created with the following ID: 76. The title of the pull request is: Add ability for all users to see approvals. For more information, go to the AWS CodeCommit console https://us-west-2.console.aws.amazon.com/codesuite/codecommit/repositories/terraform-multi-account/pull-requests/76?region=us-west-2.\",\"destinationReference\":\"refs/heads/master\",\"callerUserArn\":\"arn:aws:iam::799546647898:user/kschlorff\",\"creationDate\":\"Wed Mar 11 16:37:45 UTC 2020\",\"pullRequestId\":\"76\",\"title\":\"Add ability for all users to see approvals\",\"revisionId\":\"964dc5f4df5335c45877aae7911adeac98ea6d23104ecfdea815154e2fec48bf\",\"repositoryNames\":[\"terraform-multi-account\"],\"destinationCommit\":\"31492854843c2e7d2db77df7d668caa3ef4aa875\",\"event\":\"pullRequestCreated\",\"sourceCommit\":\"1b3e002e0f2c3529a6e9ef5ad93a88d41c592322\"},\"resources\":[\"arn:aws:codecommit:us-west-2:799546647898:terraform-multi-account\"],\"additionalAttributes\":{}}",
                   "Timestamp": "2019-11-07T21:43:00.207Z",
                   "SignatureVersion": "1",
                   "Signature": "hcpKXuncSl7DK7wOH8d8JvZLu3xfe8NH6F+CQNc6iOFIrraIuaPh4bvj6paOYGNKSAYTBcVATbbAgUR4puDhiym7CF2Rnh8XhaCwpliaWPuZjGfdcZ71OJTb78jVaUR/c36xT+EmTx/EnBhX0P1A1DQT9/xabFMBH7Kq3h4gR6145nJoRKnRlOXHXCvq9Rs+29if5dNlsQphb4ob54CGES/Kow2ZYidibZFmgOjF2A5nwU4GE9hg5q8pmxJI/NkoKv/4wAFCa1TrYnsvfqVlbha1JIAj/oMv6sie2emmPpu4zUy70ZBkt1h9giYeggsbqIY0Vbm4qUxq8Igw+FyKRA==",
                   "SigningCertUrl": "https://sns.us-west-2.amazonaws.com/SimpleNotificationService-6aad65c2f9911b05cd53efda11f913f9.pem",
                   "UnsubscribeUrl": "https://sns.us-west-2.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:us-west-2:000000000000:CodeCommitPullRequestComments:1f05f39c-05c6-4787-a60b-89c4a4a80bae",
                   "MessageAttributes": {}
                 }
               }
             ]
           }"""

  val snsCommentOnPullRequest =
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

  behavior of "sns wrapper deserialization"

  it should "decode the input with the message as decodable type" in {
    val expected: List[Notification] = {
      Notification("A pull request event occurred in the following AWS CodeCommit repository: terraform-multi-account. User: arn:aws:iam::799546647898:user/kschlorff. Event: Created. Pull request name: 76. Additional information: A pull request was created with the following ID: 76. The title of the pull request is: Add ability for all users to see approvals. For more information, go to the AWS CodeCommit console https://us-west-2.console.aws.amazon.com/codesuite/codecommit/repositories/terraform-multi-account/pull-requests/76?region=us-west-2.",
        "Add ability for all users to see approvals".pure[Option]
      )
    }.pure[List]

    val parsed =
      messagesInRecordsTraversal[Notification](snsPullRequestStateChange)

    parsed should be(expected)
  }

  it should "decode a PR comment (which doesn't have a title)" in {
    val parsed = messagesInRecordsTraversal[Notification](snsCommentOnPullRequest)
    val expected = Notification("A pull request event occurred in the following AWS CodeCommit repository: terraform-multi-account. The user: arn:aws:iam::799546647898:user/aquintano made a comment or replied to a comment. The comment was made on the following Pull Request: 92. For more information, go to the AWS CodeCommit console https://us-west-2.console.aws.amazon.com/codesuite/codecommit/repositories/terraform-multi-account/pull-requests/92/activity#c5b1a617-9987-407d-8f5e-34c585359c1c%3A0bd1624c-7c42-46ee-9cc1-10dfab32d47d?region=us-west-2",
      None
    )

    parsed should have size 1
    parsed should contain(expected)
  }

  behavior of "optics traversal"

  it should "not blow up if the json doesn't match the structure" in {
    val json = json"{}"

    messagesInRecordsTraversal[Json](json) should be(empty)
  }

  it should "not blow up if the message is unparsable" in {
    val json =
      json"""{
               "Records": [
                 {
                   "Sns": {
                     "Message": "well-nigh unparsable"
                   }
                 }
               ]
          }"""

    val output = messagesInRecordsTraversal[Json](json)
    output should be(empty)
  }

}
