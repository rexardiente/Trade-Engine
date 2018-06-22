package mrtrade

import java.util.UUID
import play.api.libs.json._

case class OrderResultJson(
    id_order: UUID,
    id_account: UUID,
    code: String,
    size: String,
    price: String,
    is_sell: Boolean,
    code_currency_base: String,
    code_currency_counter: String)

object OrderResultJson {
  implicit val orderResultJsonFormat = Json.format[OrderResultJson]
}

case class OrderResult(
    id: UUID,
    ownerID: UUID,
    code: OrderCode.Value,
    size: BigDecimal,
    price: BigDecimal,
    sell: Boolean) {

  def toOutputJson(pair: (String, String)) = {
    Json.toJson(new OrderResultJson(
        id,
        ownerID,
        code.toString,
        size.toString,
        price.toString,
        sell,
        pair._1,
        pair._2))
  }

  def totalAmount: BigDecimal = price * size
}

object OrderResult {
  implicit val orderResultFormat = Json.format[OrderResult]

  def fromOrder(order: Order, code: OrderCode.Value) =
    new OrderResult(order.id, order.ownerID, code, order.size, order.price, order.sell)
}

case class SqlOrderResult(
  idOrder: String,
  idAccountRef: String,
  tradeType: String,
  size: String,
  price: String,
  side: String,
  idCurrencyBase: String,
  idCurrencyCounter: String,
  isSuccessful: String
)
