package utils

import java.util.UUID
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }
import scala.collection.mutable.Queue
import play.api.libs.json._
import models.service.PairlistService
import mrtradelibrary.models.repo.TransactionsRepo
import mrtradelibrary.models.domain.{ Transactions, Pairlist, Message, Response, MessagePairlist }
import mrtradelibrary.models.util.Clients.pairlistList
import mrtradelibrary.utils.Currencies
import mrtrade.{ Order, OrderBook, OrderResult, OrderResultJson, OrderResultSet, SqlOrderResult }
import mrtradelibrary.utils._
import mrtradelibrary.models.util.Clients._

class TransactionsProcessor (
    orderBook: OrderBook,
    pairlistService: PairlistService,
    transactionsRepo: TransactionsRepo)
    (protected implicit val ec: ExecutionContext) {
  import utils.PairlistUtility

  private val pendingOrders = new Queue[Order]
  private val pendingResults = new Queue[OrderResultSet]
  private var lastIndex = 0
  private var bookReloaded = false

  //repopulate the order book based on snapshots!
  reloadOrderBook

  def tick = {
    if (bookReloaded) {
      readProcessWrite

      //TODO: write snapshots
    }
  }

  def getLastIndex: Int = lastIndex

  //Reads the transaction table
  private def readProcessWrite = {
    //Run the Query to select rows >lastIndex
    transactionsRepo.getNewTransactionRequests(lastIndex).onComplete {
      case Failure(e) => e.printStackTrace
      case Success(newTransactions) => {
        //Set lastIndex to the highest index returned
        lastIndex = {
          if (newTransactions.isEmpty) 0
          else newTransactions.map(_.id).max
        }
        //Add the rows into processing queue
        addOrders(newTransactions)
        //Begin Processing the new orders
        processOrders
        //Write out the results
        writeTransactionResults
      }
    }
  }

  private def reloadOrderBook = {
    //TODO Read snapshot table first

    //Fetch from zero
    transactionsRepo.getNewTransactionRequests(0).onComplete {
      case Failure(e) => e.printStackTrace
      case Success(newTransactions) => {
        //Set lastIndex to the highest index returned
        lastIndex = {
          if (newTransactions.isEmpty) 0
          else newTransactions.map(_.id).max
        }
        //Add the rows into processing queue
        addOrders(newTransactions)
        //Begin Processing the new orders
        processOrders
        transactionsRepo.getCompletedTransactions[Transactions].onComplete {
          case Failure(e) => e.printStackTrace
          case Success(completedTransactions) => {
            val comp = List(completedTransactions.map(Order.fromTransactions(_)): _*)
            //Get the remaining order results
            val remaining = pendingResults.diff(comp)

            //New (unwritten) order results were found
            //ie: Trades were posted while trade engine was down
            if (!remaining.isEmpty) {
              //Clear the list...
              pendingResults.clear
              //But keep what is remaining
              pendingResults ++= remaining

              println("New results written: " + remaining.length)
              writeTransactionResults //Write them out to the DB
            }

            //Allow future ticks + processing
            bookReloaded = true
          }
        }
      }
    }
  }

  private def writeTransactionResults() = {
    while (!pendingResults.isEmpty) {
      for {
        orderResult <- {
          transactionsRepo
            .updateBalance(pendingResults.dequeue.toOutputJson(orderBook.pair).mkString(","))
            .map(_.map(ConvertStringToObject(_, SqlOrderResult).asInstanceOf[SqlOrderResult]))
        }

        convert <- Future.successful {
          for {
            fillOrPartiallyFill <-  List {
              orderResult.collect {
                case sor: SqlOrderResult =>
                  if (sor.isSuccessful == "t" &&
                      sor.tradeType == "FILL" || sor.tradeType == "PARTIALLY_FILL")
                  sor
              }.collect { case sor: SqlOrderResult => sor }
            }

            cancelled <-  List {
              orderResult.collect {
                case sor: SqlOrderResult =>
                  if (sor.isSuccessful == "t" && sor.tradeType == "CANCELLED") sor
              }.collect { case sor: SqlOrderResult => sor }
            }
          } yield (fillOrPartiallyFill, cancelled)
        }

        _ <- Future.successful {
          if (!convert.flatMap(_._1).isEmpty) {
            val plu = new PairlistUtility

            pairlistService.pairlistStatistics("[" +
              Json.toJson(Pairlist(
                plu.currencyEnum(orderBook.pair._1).getOrElse(Currencies.Xrp),
                plu.currencyEnum(orderBook.pair._2).getOrElse(Currencies.Jpy)
              )).toString + "]"
            ).map({ res =>
              pairlistList
                .map(_._2 ! Json.toJson(MessagePairlist(Transaction.pairlist.toString, res)))
            })
          }

          if (!convert.flatMap(_._2).isEmpty) {
            convert.flatMap(_._2).map({ cx =>
              val idAccountRef = UUID.fromString(cx.idAccountRef)
              cancel_orders
                .find(_._1 == idAccountRef)
                .map { u =>
                  val cancelMessage = Transaction.cancelOrder.toString
                  val status = TransactionMessage.failOrSuccess(cx.isSuccessful)
                  val message =
                    Json.toJson(Message(cancelMessage,
                    Response(status, TransactionMessage.description(cancelMessage, status))))
                  u._2 ! message
                  cancel_orders -= idAccountRef
                }
            })
          }
        }
      } yield ()
    }
  }

  //Add orders into the pending orders queue
  private def addOrders(in: Seq[Transactions]) =
    pendingOrders ++= Queue(in.map(Order.fromTransactions(_)): _*)

  //Send the orders off for processing
  private def processOrders = {
    while (!pendingOrders.isEmpty) {
      processOrder(pendingOrders.dequeue)
    }
  }

  //Helper
  //Process the order individually
  private def processOrder(order: Order) = {
    pendingResults += orderBook.process(order)
  }
}
