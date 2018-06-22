package mrtradelibrary.models.domain

import java.util.UUID
import java.time.Instant
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class Transactions(
    id: Int,
    idOrder: UUID,
    idAccountRef: UUID,
    tradeType: Trades.Value,
    side: Sides.Value,
    price: BigDecimal,
    amount: BigDecimal,
    idCurrencyBase: Currencies.Value,
    idCurrencyCounter: Currencies.Value,
    createdAt: Instant)
  extends RequestValidation {
    def size = amount / price
    //def totalAmount = price * size
  }

object Transactions extends SideDeserializer with TradeDeserializer with CurrencyDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Transactions] = (
    (__ \ "id").format[Int] and
    (__ \ "id_order").format[UUID] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "trade_type").format[Trades.Value] and
    (__ \ "side").format[Sides.Value] and
    (__ \ "price").format[BigDecimal] and
    (__ \ "amount").format[BigDecimal] and
    (__ \ "id_currency_base").format[Currencies.Value] and
    (__ \ "id_currency_counter").format[Currencies.Value] and
    (__ \ "created_at").format[Instant]
  )(Transactions.apply _, unlift(Transactions.unapply _))
}
