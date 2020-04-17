package com.dwolla.codestar.notifications

import cats._
import cats.data._
import cats.implicits._

object TestEnvironment {
  def apply[F[_] : Applicative](maybeSender: Option[String],
                                maybeRecipient: Option[String]): Environment[F] =
    new TestEnvironment[F](maybeSender, maybeRecipient)
}

class TestEnvironment[F[_] : Applicative](maybeSender: Option[String],
                                          maybeRecipient: Option[String]) extends Environment[F] {
  override def get(key: String): F[Option[String]] = key match {
    case "SENDER_ADDRESS" => maybeSender.pure[F]
    case "RECIPIENT_ADDRESS" => maybeRecipient.pure[F]
    case _ => none[String].pure[F]
  }

  override def get(key: String, default: => String): F[String] =
    OptionT(get(key)).getOrElse(default)
}

