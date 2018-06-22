package mrtrade

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.models.util.ValueDeserializer

case class SimpleOrder(price: BigDecimal, size: BigDecimal)

object SimpleOrder {
  implicit val SimpleOrderFormats: Format[SimpleOrder] = Json.format[SimpleOrder]
}
