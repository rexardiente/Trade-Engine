package mrtrade

import java.util.UUID

case class QueuedOrder (id: UUID, ownerID: UUID, var size: BigDecimal, sell: Boolean) {
  def consumePartialOrder(price: BigDecimal, inSize: BigDecimal) = {
    if (inSize >= size) {
      throw new Exception("BAD CONSUME ORDER") //Should call consumeWholeOrder
    } else {
      size -= inSize
      new OrderResult(id, ownerID, OrderCode.PARTIALLY_FILL, inSize, price, sell)
    }
  }

  def consumeWholeOrder(price: BigDecimal) =
    new OrderResult(id, ownerID, OrderCode.FILL, size, price, sell)

  def toOrderBookSummary = "(" + id + "," + ownerID + "," + size + "," + ")"
}

object QueuedOrder {
  def fromOrder(order: Order): QueuedOrder =
    new QueuedOrder(order.id, order.ownerID, order.size, order.sell)
}