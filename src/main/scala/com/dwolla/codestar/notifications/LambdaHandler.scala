package com.dwolla.codestar.notifications

import cats.effect._
import cats.implicits._
import com.amazonaws.services.lambda.runtime.Context
import com.dwolla.lambda._
import com.dwolla.sns.model._
import io.chrisdavenport.log4cats.Logger
import io.circe._
import natchez.Trace

class LambdaHandler(printer: Printer) extends IOLambda[Json, Unit](printer) {
  def this() = this(Printer.noSpaces)

  override def handleRequestF[F[_] : Concurrent : ContextShift : Logger : Timer : Trace](blocker: Blocker)
                                                                                        (input: Json, context: Context): F[LambdaResponse[Unit]] =
    for {
      _ <- messagesInRecordsTraversal[String](input).traverse_(Logger[F].info(_))
    } yield ()
}
