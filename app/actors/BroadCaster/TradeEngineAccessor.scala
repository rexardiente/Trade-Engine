package actors

import javax.inject.{ Inject, Singleton, Named }
import java.util.UUID
import akka.actor._
import play.api.Logger
import play.api.libs.json._
import mrtradelibrary.models.domain.Notification
import com.typesafe.config.ConfigFactory
import mrtradelibrary.utils.Transaction._
import mrtradelibrary.models.util.Clients._
import modules.OrderBookManagerModule

object TradeEngineAccessor {
  def props = Props[TradeEngineAccessor]
}

@Singleton
class TradeEngineAccessor @Inject()(
    obm: OrderBookManagerModule
) extends Actor {

  override def preStart(): Unit = {
    super.preStart()
  }

  def receive = {
    case notification: Notification =>
      if (notification.command == cancelOrder.toString) {
        cancel_orders += (notification.idAccountRef -> notification.actorRef)
      }

      obm.orderBooks.values.map(_.tick)

    case other =>
      Logger.info(s"TradeEngineAccessor: $other")
  }
}
