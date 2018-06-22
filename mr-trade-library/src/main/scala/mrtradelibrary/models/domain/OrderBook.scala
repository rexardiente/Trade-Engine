package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils._
import mrtradelibrary.models.util._

case class OrderBook(
    command: Transaction.Value,
    currencyBase: Currencies.Value,
    currenycCounter: Currencies.Value)
  extends mrtradelibrary.models.util.RequestValidation

object OrderBook extends ValueDeserializer with CurrencyDeserializer {
  implicit val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[OrderBook] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "currency_base").format[Currencies.Value] and
    (__ \ "currency_counter").format[Currencies.Value]
  )(OrderBook.apply _, unlift(OrderBook.unapply _))
}
