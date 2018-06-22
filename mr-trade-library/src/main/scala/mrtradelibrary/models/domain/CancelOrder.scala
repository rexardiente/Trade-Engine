package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class CancelOrder(
  command: Transaction.Value,
  idAccountRef: UUID,
  side: Sides.Value,
  amount: BigDecimal,
  price: BigDecimal,
  currencyBase: Currencies.Value,
  currencyCounter: Currencies.Value,
  idOrder: UUID) extends RequestValidation

object CancelOrder extends ValueDeserializer with CurrencyDeserializer with SideDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[CancelOrder] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "side").format[Sides.Value] and
    (__ \ "amount").format[BigDecimal] and
    (__ \ "price").format[BigDecimal] and
    (__ \ "id_currency_base").format[Currencies.Value] and
    (__ \ "id_currency_counter").format[Currencies.Value] and
    (__ \ "id_order").format[UUID]
  )(CancelOrder.apply _, unlift(CancelOrder.unapply _))
}
