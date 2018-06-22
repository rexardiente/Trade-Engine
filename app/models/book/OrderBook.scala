package mrtrade

import scala.concurrent.ExecutionContext.Implicits.global
import java.time.Instant
import utils.TransactionsProcessor
import models.service.PairlistService
import mrtradelibrary.models.repo.{ TransactionsRepo, OrderBookSummaryRepo }
import mrtradelibrary.models.domain.OrderBookSummary
import play.api.libs.json.{ Json, JsString }

object OrderBookType extends Enumeration {
  type OrderBookType = Value
  val BIDS, ASKS = Value
}

class OrderBook(
    val pair: (String, String),
    pls: PairlistService,
    tr: TransactionsRepo,
    obsr: OrderBookSummaryRepo) {
  import mrtrade.OrderBookType._
  private val transactionsProcessor = new TransactionsProcessor(this, pls, tr)
  private val bids: OrderList = new OrderList(BIDS)
  private val asks: OrderList = new OrderList(ASKS)

  var tickCounter = 0
  val summaryTicks = 5

  def tick = {
    transactionsProcessor.tick

    tickCounter += 1

    if (tickCounter > summaryTicks) {
      println("Writing summary table")
      obsr.add(toOrderBookSummary)
      tickCounter -= summaryTicks
    }
  }

  //Processes the incomming order,
  //and returns the list all modified orders
  def process(order: Order): OrderResultSet = {
    if (order.isCancel)
      processCancelOrder(order)
    else
      processNormalOrder(order)
  }

  private def processCancelOrder(order: Order): OrderResultSet = {
    if (order.sell)
      asks.cancelOrder(order)
    else
      bids.cancelOrder(order)
  }

  private def processNormalOrder(order: Order): OrderResultSet = {
      // println("process order")
    // if (!asks.isEmpty)
    //   println("ask: " + getAsk.price)
    // if (!bids.isEmpty)
    //   println("bid: " + getBid.price)
    if (order.price < 0) //negative price represents a market order
      if (order.sell) bids.processMarketOrder(order)
      else asks.processMarketOrder(order)
    else if (!order.sell)
      if (asks.isEmpty || order.price < getAsk.price) bids.place(order)
      else processLimitBuyAboveAsk(order)
    else
      if (bids.isEmpty || order.price > getBid.price) asks.place(order)
      else processLimitSellBelowBid(order)
  }

  //Process a limit buy placed above the ask price (rare)
  //and returns the list of all modified orders
  private[this] def processLimitBuyAboveAsk(order: Order): OrderResultSet = {
    //If the head object of asks.process(order) is filled, then its no problem
    //But we need to handle a special case if this order is partially filled
    //Example:  Ask $1 for 3 units, Ask $2 for 1 units, Ask $3 for 5 units.
    //          user places limit buy for 6 units @ $2. -> 3 filled at 1$
    //          but only 1 unit filled at $2, so his order has 2 units left at $2
    val result = asks.processSpecialLimitOrder(order)

    if (result._1 == 0d)
      result._2 //The limit order was completely consumed
    else
      new OrderResultSet(result._2.results ++
        bids.place(order.copy(size = result._1)).results)
  }

  //Process a limit sell placed below the bid price (rare)
  //and returns the list of all modified orders
  private[this] def processLimitSellBelowBid(order: Order): OrderResultSet = {
    val result = bids.processSpecialLimitOrder(order)
    if (result._1 == 0d)
      result._2 //The limit order was completely consumed
    else
      new OrderResultSet(result._2.results ++
        asks.place(order.copy(size = result._1)).results)
  }

  //Returns the list of all bids (for L2 data)
  def getBids(levels: Option[Int]): List[SimpleOrder] =
    if (levels.isEmpty)
      bids.getSimpleOrderList
    else
      bids.getSimpleOrderList(levels.get)

  //Returns the list of all asks (for L2 data)
  def getAsks(levels: Option[Int]): List[SimpleOrder] =
    if (levels.isEmpty)
      asks.getSimpleOrderList
    else
      asks.getSimpleOrderList(levels.get)

  //Returns the list of top X bids (for L2 data)
  // def getBids(levels: Int): List[SimpleOrder] = bids.getSimpleOrderList(levels)

  //Returns the list of top X asks (for L2 data)
  // def getAsks(levels: Int): List[SimpleOrder] = asks.getSimpleOrderList(levels)

  //Returns the highest bid (for L1 data)
  def getBid(): SimpleOrder = bids.getFirstSimpleOrder

  //Returns the lowest ask (for L1 data)
  def getAsk(): SimpleOrder = asks.getFirstSimpleOrder

  //Returns the current spread (for L1 data)
  def getSpread(): BigDecimal = getAsk.price - getBid.price

  def toOrderBookSummaryData(): String = {
    var output = Json.stringify(Json.obj(
      "pair" -> JsString(pair._1 + "/" + pair._2),
      "last" -> transactionsProcessor.getLastIndex))

    output = output.dropRight(1) //Drop the last "}"
    output = output + ", \"bids\": [" + bids.toOrderBookSummary + "]"
    output = output + ", \"asks\": [" + asks.toOrderBookSummary + "]}"
    output
  }

  def toOrderBookSummary() = new OrderBookSummary(0, Instant.now, toOrderBookSummaryData)
}