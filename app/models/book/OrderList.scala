package mrtrade

import scala.collection.mutable.{ SortedMap, ListBuffer }
import scala.math.Ordering
import scala.util.control.Breaks._

class OrderList(orderBookName: OrderBookType.Value) {
  //Asks should be sorted ascending (default)
  //Bids should be sorted descending (reversed)
  private val orderQueues =
    if (orderBookName == OrderBookType.BIDS)
      SortedMap.empty[BigDecimal, OrderQueue](Ordering.BigDecimal.reverse)
    else
      SortedMap.empty[BigDecimal, OrderQueue]

  //Processes the incomming market order
  def processMarketOrder(order: Order): OrderResultSet = {
    var remainingSize = order.size //Tracking progress
    val orderResults = ListBuffer[OrderResult]() //What we will return

    val priceLevels = orderQueues.keys.toList

    if (priceLevels.length == 0) { //No orders to match against!
      orderResults.prepend(OrderResult.fromOrder(order, OrderCode.CANCELLED)) //Return the marked order cancelled
    } else { //We have prices to match
      breakable { for (price <- priceLevels if remainingSize > 0) { //Loop through each price level
          getOrderQueue(price) match { //Loop through each price level
            case q: OrderQueue => {
              val depth = q.getDepth
              if (depth <= remainingSize) { //We will consume the whole level
                remainingSize -= depth //Reduce remaining size
                orderResults ++= q.createConsumedOrdersAll(price) //Append the results
                orderQueues.remove(price) //Pop the price level
              } else { //There is enough depth that we need to consume indivudal orders
                //Process and get the results
                orderResults ++= q.processIndividualOrders(price, remainingSize)
                remainingSize = 0 //Reduce remaining size
                break
              }
            }
            case _ =>
          }
        }
      }
    }

    if (orderResults.length > 0) { //There was some orders consumed afterall..
      val marketOrderPrice = calculateMarketOrderPrice(orderResults)
      if (remainingSize == 0) { //Order completely filled
        orderResults.prepend(
          OrderResult(
              order.id,
              order.ownerID,
              OrderCode.FILL,
              order.size,
              marketOrderPrice,
              order.sell))
      } else if (remainingSize != order.size) { //Order partially filled
        orderResults.prepend( //Inform about the filled parts
          OrderResult(
              order.id,
              order.ownerID,
              OrderCode.PARTIALLY_FILL,
              order.size - remainingSize,
              marketOrderPrice,
              order.sell))
        orderResults.prepend( //But cancel the unfilled order
          OrderResult(
              order.id,
              order.ownerID,
              OrderCode.CANCELLED,
              remainingSize,
              order.price,
              order.sell))
      }
    }

    OrderResultSet(orderResults.toList)
  }

  //Processes the incomming special limit order
  def processSpecialLimitOrder(order: Order): (BigDecimal, OrderResultSet) = {
    var remainingSize = order.size //Tracking progress
    val orderResults = ListBuffer[OrderResult]() //What we will return

    val priceLevels = orderQueues.keys.toList

    if (priceLevels.length == 0) { //No orders to match against!
      orderResults.prepend(OrderResult.fromOrder(order, OrderCode.CANCELLED)) //Return the marked order cancelled
    } else { //We have prices to match
      breakable { for (price <- priceLevels if remainingSize > 0) {
          //Early out from price
          if (!((orderBookName == OrderBookType.BIDS && price.toDouble >= order.price) ||
              (orderBookName == OrderBookType.ASKS && price.toDouble <= order.price)))
              break

          getOrderQueue(price) match { //Loop through each price level
            case q: OrderQueue => {
              val depth = q.getDepth
              if (depth <= remainingSize) { //We will consume the whole level
                remainingSize -= depth //Reduce remaining size
                orderResults ++= q.createConsumedOrdersAll(price) //Append the results
                orderQueues.remove(price) //Pop the price level
              } else { //There is enough depth that we need to consume indivudal orders
              //Process and get the results
                orderResults ++= q.processIndividualOrders(price, remainingSize)
                remainingSize = 0 //Reduce remaining size
                break
              }
            }
            case _ =>
          }
        }
      }
    }

    val marketOrderPrice = calculateMarketOrderPrice(orderResults)

    if (remainingSize == 0) { //Order completely filled
      orderResults.prepend(
        OrderResult(
            order.id,
            order.ownerID,
            OrderCode.FILL,
            order.size,
            marketOrderPrice,
            order.sell))
    } else if (remainingSize != order.size) { //Order partially filled
      orderResults.prepend(
        OrderResult(
            order.id,
            order.ownerID,
            OrderCode.PARTIALLY_FILL,
            order.size - remainingSize,
            marketOrderPrice,
            order.sell))
    }

    (remainingSize, OrderResultSet(orderResults.toList))
  }

  //Place an order within the orderbook
  def place(order: Order): OrderResultSet = {
    getOrderQueue(order.price) match {
      case q: OrderQueue =>
        q.appendOrder(order)
      case _ => {
        val q = new OrderQueue
        orderQueues += (order.price -> q)
        q.appendOrder(order)
      }
    }
  }

  //Returns the total depth for the specified price level
  def getDepth(price: BigDecimal): BigDecimal = {
    getOrderQueue(price) match {
      case q: OrderQueue => q.getDepth
      case _ => 0
    }
  }

  //Returns the total depth up to the specified price level
  def getTotalDepth(price: BigDecimal): BigDecimal = {
    ??? //TODO - Maybe use sortedMap.filterKeys(****)
  }

  def getSimpleOrderList: List[SimpleOrder] =
    orderQueues.map(queue =>
      SimpleOrder(queue._1, queue._2.getDepth)).toList

  //Returns a list of X price levels as a SimpleOrder, Price + Depth
  def getSimpleOrderList(levels: Int): List[SimpleOrder] =
    orderQueues.take(levels).map(queue =>
      SimpleOrder(queue._1, queue._2.getDepth)).toList

  def getFirstSimpleOrder: SimpleOrder = {
    val head = orderQueues.head
    SimpleOrder(head._1, head._2.getDepth)
  }

  def cancelOrder(order: Order): OrderResultSet = {
    val priceLevels = orderQueues.keys.toList
    val results = ListBuffer[OrderResult]()

    breakable { for (price <- priceLevels) { //Loop through each price level
      getOrderQueue(price) match { //Loop through each price level
        case q: OrderQueue => {
          val result = q.tryCancelOrder(order)
          if (result._1 == true) { //We cancelled the order
            results += result._2.get

            if (q.isEmpty) //We popped the last order, so remove this price level
              orderQueues.remove(price)

            break
          }
        }
        case _ =>
      }
    }}
    OrderResultSet(results.toList)
  }

  private def getOrderQueue(price: BigDecimal) =
    orderQueues.get(price).getOrElse(None)

  def isEmpty: Boolean = orderQueues.isEmpty

  private def calculateMarketOrderPrice(results: ListBuffer[OrderResult]): BigDecimal = {
    val filteredList = results.filter(or =>
      (or.code == OrderCode.FILL || or.code == OrderCode.PARTIALLY_FILL))

    val totalSize = filteredList.map(_.size).sum
    val totalAmount = filteredList.map(_.totalAmount).sum

    totalAmount / totalSize
  }

  def toOrderBookSummary: String = {
    var output = ""
    val priceLevels = orderQueues.keys.toList

    for (price <- priceLevels) {
      getOrderQueue(price) match { //Loop through each price level
        case q: OrderQueue => {
          if (!output.isEmpty && output.last == '}')
            output += ","

          output += "{\"" + price + "\": " + q.toOrderBookSummary + "}"
        }
        case _ =>
      }
    }
    output
  }
}
