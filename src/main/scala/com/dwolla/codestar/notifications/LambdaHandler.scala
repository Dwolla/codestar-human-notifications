package com.dwolla.codestar.notifications

import cats._
import cats.effect._
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import com.dwolla.codestar.notifications.model._
import com.dwolla.lambda._
import com.dwolla.ses.SesAlg
import com.dwolla.sns.model._
import io.chrisdavenport.log4cats.Logger
import io.circe._
import natchez.Trace

class LambdaHandler(printer: Printer) extends IOLambda[Json, Unit](printer) {
  def this() = this(Printer.noSpaces)

  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (input: Json, context: Context): F[LambdaResponse[Unit]] =
    SesAlg.resource[F]
      .use(new Handler[F](_)(input))
}

class Handler[F[_]](ses: SesAlg[F]) {
  def apply(input: Json)
           (implicit E: Environment[F], F: Monad[F]): F[LambdaResponse[Unit]] =
    for {
      senderAddress <- Environment[F].get("SENDER_ADDRESS", "dev-testing@us-west-2.sandbox.dwolla.net")
      recipientAddress <- Environment[F].get("RECIPIENT_ADDRESS", "dev-testing@dwolla.com")
      _ <- messagesInRecordsTraversal[CodeStarRecord](input).traverse_ {
        case Notification(body, title) =>
          ses.sendEmail(recipientAddress, senderAddress, title.getOrElse("CodeStar Notification"), body)
        case UnparsableRecord(json) =>
          ses.sendEmail(recipientAddress, senderAddress, "CodeStar Notification", json.spaces2)
      }
    } yield ()
}
