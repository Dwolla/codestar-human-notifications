package com.dwolla.ses

import cats.effect._
import cats.implicits._
import software.amazon.awssdk.services.ses.SesAsyncClient
import com.dwolla.fs2aws.AwsEval._
import io.chrisdavenport.log4cats.Logger
import software.amazon.awssdk.services.ses.model.{Body, Content, Destination, Message, SendEmailRequest}

trait SesAlg[F[_]] {
  def sendEmail(to: String,
                from: String,
                subject: String,
                body: String,
               ): F[Unit]
}

object SesAlg {
  def resource[F[_] : Concurrent : Logger]: Resource[F, SesAlg[F]] =
    Resource.fromAutoCloseable(Sync[F].delay {
      SesAsyncClient.builder().build()
    }).map { client =>
      new SesAlg[F] {
        private def destination(to: String): Destination =
          Destination.builder().toAddresses(to).build

        private def content(s: String): Content =
          Content.builder().data(s).charset("UTF-8").build

        private def message(subject: String, body: String): Message =
          Message.builder()
            .subject(content(subject))
            .body(Body.builder().text(content(body)).build())
            .build()

        override def sendEmail(to: String, from: String, subject: String, body: String): F[Unit] = {
          val req: SendEmailRequest = SendEmailRequest.builder()
            .destination(destination(to))
              .source(from)
              .message(message(subject, body))
              .build()

          for {
            res <- eval[F](req)(client.sendEmail)(_.messageId())
            _ <- Logger[F].info(s"Sent message $res")
          } yield ()
        }
      }
    }
}