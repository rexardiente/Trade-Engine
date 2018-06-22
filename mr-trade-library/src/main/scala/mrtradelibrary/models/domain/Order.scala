package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class Order(
    command: Transaction.Value,
    idAccountRef: UUID,
    side: Sides.Value,
    amount: BigDecimal,
    price: BigDecimal,
    currencyBase: Currencies.Value,
    currencyCounter: Currencies.Value)
  extends RequestValidation

object Order extends ValueDeserializer with CurrencyDeserializer with SideDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Order] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "side").format[Sides.Value] and
    (__ \ "amount").format[BigDecimal] and
    (__ \ "price").format[BigDecimal] and
    (__ \ "id_currency_base").format[Currencies.Value] and
    (__ \ "id_currency_counter").format[Currencies.Value]
  )(Order.apply _, unlift(Order.unapply _))
}
