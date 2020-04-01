package com.dwolla.codestar.notifications

import cats.implicits._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

package object model {

}

package model {

  import io.circe.Decoder.Result

  sealed trait CodeStarRecord
  case class Notification(notificationBody: String,
                          title: Option[String],
                         ) extends CodeStarRecord
  case class UnparsableRecord(json: Json) extends CodeStarRecord

  object CodeStarRecord {
    implicit val codeStarRecordDecoder: Decoder[CodeStarRecord] =
      Decoder[Notification].widen[CodeStarRecord] or new Decoder[UnparsableRecord] {
        override def apply(c: HCursor): Result[UnparsableRecord] = Right(UnparsableRecord(c.value))
      }.widen[CodeStarRecord]

    implicit val codeStarRecordEncoder: Encoder[CodeStarRecord] = {
      case n: Notification => n.asJson
      case UnparsableRecord(json) => json
    }
  }

  object Notification {
    implicit val notificationCodec: Codec[Notification] =
      deriveCodec
  }
}
