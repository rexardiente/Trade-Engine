package actors

import javax.inject._
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.actor._
import play.api.Logger
import play.api.libs.json._
import mrtrade.{ OrderBook, SimpleOrder }
import mrtradelibrary.models.util.Clients._
import mrtradelibrary.models.domain.{ AllOrders, PairlistRequest, PairlistSetResult, MessagePairlist }
import mrtradelibrary.utils.Transaction
import modules.OrderBookManagerModule
import models.service.PairlistService

object AccountServiceActor {
  def props = Props[AccountServiceActor]
}

@Singleton
class AccountServiceActor @Inject()(
    pairlistService: PairlistService,
    obm: OrderBookManagerModule
) extends Actor {
  import utils.PairlistUtility

  override def preStart() = {
    super.preStart()
  }

  def receive: Receive = {
    case allOrders: AllOrders =>
    println("all orders request ")
      (for {
          bids <- Future.successful {
            try {
              obm.orderBooks
                .get((allOrders.currencyBase.toString, allOrders.currencyCounter.toString))
                .get.getBids(allOrders.levels)
            }
            catch { case e: Exception => List(SimpleOrder(0.0, 0.0)) }
          }
          asks <- Future.successful {
            try {
              obm.orderBooks
                .get((allOrders.currencyBase.toString, allOrders.currencyCounter.toString))
                .get.getAsks(allOrders.levels)
            }
            catch { case e: Exception => List(SimpleOrder(0.0, 0.0)) }
          }

          JSON <- Future.successful {
            Json.obj(
              "currency_base" -> allOrders.currencyBase,
              "currency_counter" -> allOrders.currencyCounter,
              "bids" -> bids,
              "asks" -> asks)
          }
        } yield (JSON)
      ).map { res =>
        order_book
          .find(_._1 == allOrders.idAccountRef)
          .map { u =>
            u._2 ! Json.obj("message"-> allOrders.command, "response" -> res)
            order_book -= allOrders.idAccountRef
          }
      }

    case pairlist: PairlistRequest =>
      val plu = new PairlistUtility

      plu.getCurrencyPair.map{ currencyPair =>
        pairlistService.pairlistStatistics(Json.toJson(currencyPair).toString)
        .map { res =>
          pairlistList
            .find(_._1 == pairlist.idAccountRef)
            .map(_._2 ! Json.toJson(MessagePairlist(Transaction.pairlist.toString, res)))
        }
      }

    case json: JsValue =>
      println(json)

    case other =>
      println("INVALID REQUEST.")

  }
}
