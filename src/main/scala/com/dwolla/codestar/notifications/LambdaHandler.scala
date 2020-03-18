package com.dwolla.codestar.notifications

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

//  val fromAddress = "noreply@dwolla.com"
  val fromAddress = "dev-testing@dwolla.com"
  val toAddress = "dev-testing@dwolla.com"

  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (input: Json, context: Context): F[LambdaResponse[Unit]] =
    SesAlg.resource[F].use { ses =>
      for {
        _ <- messagesInRecordsTraversal[CodeStarRecord](input).traverse_ {
          case Notification(body, title) =>
            ses.sendEmail(toAddress, fromAddress, title.getOrElse("CodeStar Notification"), body)
          case UnparsableRecord(json) =>
            ses.sendEmail(toAddress, fromAddress, "CodeStar Notification", json.spaces2)
        }
      } yield ()
    }
}
