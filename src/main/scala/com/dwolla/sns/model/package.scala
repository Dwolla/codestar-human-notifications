package com.dwolla.sns

import io.circe._
import io.circe.optics.JsonPath.root
import io.circe.optics.UnsafeOptics
import monocle._

package object model {
  val snsMessage: Traversal[Json, String] = root.Records.each.Sns.Message.as[String]

  private val snsMessageDetail: Optional[Json, Json] = root.detail.notificationBody.json

  private val parseStringAsJson: Prism[String, Json] =
    Prism[String, Json](parser.parse(_).toOption)(Printer.noSpaces.print)

  def messagesInRecordsTraversal[A: Decoder : Encoder](json: Json): List[A] = {
    snsMessage
      .composePrism(parseStringAsJson)
      .composeOptional(snsMessageDetail)
      .composePrism(UnsafeOptics.parse[A])
      .getAll(json)
  }

}
