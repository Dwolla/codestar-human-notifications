package com.dwolla.lambda

import java.io._

import com.dwolla.codestar.notifications.LambdaHandler
import io.circe.Printer
import io.circe.literal._
import org.slf4j.LoggerFactory

object TestRunner extends App {
  val logger = LoggerFactory.getLogger("TestRunner")

  val input: InputStream = new ByteArrayInputStream(
    json"""{
             "version": "0",
             "id": "98377d67-b006-cdc3-1363-8a0aee2316f8",
             "detail-type": "CodeCommit Comment on Pull Request",
             "source": "aws.codecommit",
             "account": "123456789012",
             "time": "2018-02-09T07:15:16Z",
             "region": "us-east-1",
             "resources": [
               "arn:aws:codecommit:us-east-1:123456789012:my-new-repository"
             ],
             "detail": {
               "beforeCommitId": "a84b513cdd26c7efea6ee9f6ccd9ad88411c6a7d",
               "repositoryId": "7dd1EXAMPLE...",
               "inReplyTo": "695bEXAMPLE...",
               "notificationBody": "An event occurred in the following repository: my-new-repository. arn:aws:iam::0123EXAMPLE0:user/codecommituser made a comment. The comment was made on the following Pull Request: 201. For more information, go to the AWS CodeCommit console https://us-east-1.console.aws.amazon.com/codecommit/home?region=us-east-1#/repository/my-new-repository/pull-request/201/activity#3276EXAMPLE...",
               "commentId": "e2b87b52-b311-447d-a909-6585f1bfa1da:6cb6f2b8-fb67-4f6a-b911-b9aa073a4ba0",
               "afterCommitId": "0307b28ec35a4ba280f7dce2a059a5424552dc53",
               "event": "commentOnPullRequestCreated",
               "repositoryName": "bholt-test",
               "callerUserArn": "arn:aws:iam::123456789012:user/codecommituser",
               "pullRequestId": "2"
             }
           }""".noSpaces.getBytes)
//  "commentId": "c1f7796b-273c-43e8-9ff1-b1853dfae106:41cb05de-a327-490b-bc34-ef9195b29da8",
  val badInput: InputStream = new ByteArrayInputStream("{Bad input".getBytes)
  val output: ByteArrayOutputStream = new ByteArrayOutputStream()

  new LambdaHandler(Printer.noSpaces).handleRequest(input, output, null)

  logger.info("{}", output)
}
