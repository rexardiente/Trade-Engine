package mrtradelibrary.models.domain

import java.time.Instant
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.models.util._

case class OrderBookSummary (
  delta: Int,
  createdAt: Instant,
  data: String
) extends RequestValidation

object OrderBookSummary {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[OrderBookSummary] = (
    (__ \ "delta").format[Int] and
    (__ \ "created_at").format[Instant] and
    (__ \ "data").format[String]
  )(OrderBookSummary.apply _, unlift(OrderBookSummary.unapply _))
}
