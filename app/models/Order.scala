package mrtrade

import java.util.UUID
import play.api.libs.json._
import scala.collection.mutable.HashMap
import mrtradelibrary.models.domain.Transactions
import mrtradelibrary.utils.{ Sides, Trades }


case class Order(
    id: UUID,
    ownerID: UUID,
    isCancel: Boolean,
    sell: Boolean,
    price: BigDecimal,
    size: BigDecimal,
    currencyBase: String,
    currencyCounter: String)

object Order {
  def fromTransactions(t: Transactions): Order =
    new Order(
      t.idOrder,
      t.idAccountRef,
      t.tradeType == Trades.Cancel,
      t.side == Sides.Sell,
      t.price,
      t.amount,
      t.idCurrencyBase.toString,
      t.idCurrencyCounter.toString)
}
