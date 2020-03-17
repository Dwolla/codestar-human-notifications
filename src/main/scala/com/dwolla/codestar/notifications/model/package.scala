package com.dwolla.codestar.notifications
import io.circe.Codec
import io.circe.generic.semiauto._

package object model {

}

package model {


  case class Notification(notificationBody: String,
                          title: Option[String],
                         )

  object Notification {
    implicit val notificationCodec: Codec[Notification] =
      deriveCodec
  }
}
