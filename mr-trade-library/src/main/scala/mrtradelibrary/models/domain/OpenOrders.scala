package mrtradelibrary.models.domain

import java.util.UUID
import java.time.Instant
import scala.math.BigDecimal._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class OpenOrders(
    command: Transaction.Value,
    idAccountRef: UUID,
    currencyBase: Currencies.Value,
    currencyCounter: Currencies.Value)
  extends RequestValidation

object OpenOrders extends CurrencyDeserializer with ValueDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[OpenOrders] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "currency_base").format[Currencies.Value] and
    (__ \ "currency_counter").format[Currencies.Value]
  )(OpenOrders.apply _, unlift(OpenOrders.unapply _))
}
