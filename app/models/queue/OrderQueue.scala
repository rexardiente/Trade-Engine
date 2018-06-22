package mrtrade

import scala.collection.mutable.{ Queue, ListBuffer }

class OrderQueue {
  private val orderQueue: Queue[QueuedOrder] = Queue()

  //Processes the incomming order based on size,
  //and returns list of modified orders.
  def processIndividualOrders(price: BigDecimal, size: BigDecimal):
      ListBuffer[OrderResult] = {
    var remainingSize = size
    val orderResults = ListBuffer[OrderResult]()

    while (remainingSize > 0) { //Loop through the queue
      val order = orderQueue.head

      if (order.size <= remainingSize) { //We can consume the whole order
        orderResults.append(order.consumeWholeOrder(price))
        orderQueue.dequeue
        remainingSize -= order.size
      } else { //We can only partially consume it
        orderResults.append(order.consumePartialOrder(price, remainingSize))
        remainingSize = 0
      }
    }

    orderResults
  }

  def createConsumedOrdersAll(price: BigDecimal): List[OrderResult] = {
    orderQueue.map(_.consumeWholeOrder(price)).toList
  }

  def tryCancelOrder(order: Order): (Boolean, Option[OrderResult]) = {
    orderQueue.dequeueFirst(_.id == order.id) match {
      case Some(qo) => {
        (true, Some(new OrderResult(
          qo.id,
          qo.ownerID,
          OrderCode.CANCELLED,
          qo.size,
          order.price,
          qo.sell)))
      }
      case None => (false, None)
    }
  }

  //Returns the full depth of this price level.
  def getDepth(): BigDecimal = orderQueue.map(_.size).sum

  //Appends the order to the order queue
  def appendOrder(order: Order) = {
    orderQueue += QueuedOrder.fromOrder(order)
    new OrderResultSet(List[OrderResult](OrderResult.fromOrder(order, OrderCode.CREATE)))
  }

  def isEmpty: Boolean = orderQueue.isEmpty

  def toOrderBookSummary: String = {
    var output = "\""

    orderQueue.map(o => {
      if (output.last != '"')
        output += "/"

      output += o.toOrderBookSummary
    })

    output += "\""
    output
  }
}
