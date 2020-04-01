package com.dwolla.codestar.notifications

import cats.data._
import cats.effect._

trait Environment[F[_]] {
  def get(key: String): F[Option[String]]
  def get(key: String, default: => String): F[String]
}

object Environment {
  def apply[F[_]](implicit E: Environment[F]): E.type = E

  implicit def syncEnvironment[F[_] : Sync]: Environment[F] = new Environment[F] {
    override def get(key: String): F[Option[String]] =
      Sync[F].delay(sys.env.get(key))

    override def get(key: String, default: => String): F[String] =
      OptionT(get(key)).getOrElse(default)
  }
}
