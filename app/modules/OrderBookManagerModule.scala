package modules

import javax.inject.{ Singleton, Inject }
import scala.collection.immutable.Map
import com.typesafe.config.ConfigFactory
import mrtrade.OrderBook
import models.service.PairlistService
import mrtradelibrary.models.repo.{ TransactionsRepo, OrderBookSummaryRepo }

@Singleton
class OrderBookManagerModule @Inject()(
    pls: PairlistService,
    tr: TransactionsRepo,
    obs: OrderBookSummaryRepo) {

  val arr: Array[((String, String), OrderBook)] =
    ConfigFactory
      .load()
      .getStringList("currencyPair")
      .toArray()
      .map { r =>
        val base = r.toString.substring(0,3)
        val counter = r.toString.substring(4,7)

        (base, counter) -> new OrderBook((base, counter), pls, tr, obs)
      }

  val orderBooks: Map[(String, String), OrderBook] = arr.toMap
}
