package mrtradelibrary.models.util

import java.util.UUID
import akka.actor._
import play.api.libs.json._
import mrtradelibrary.models.util.Clients._

trait CacheUsers {
  def saveUser(json: JsValue, out: ActorRef) = {
    try {
      val command = (json \ "command").as[String]
      val idAccountRef = (json \ "id_account_ref").as[UUID]

      command match {
        case "open_orders" =>
          open_orders += (idAccountRef -> out)

        case "order" =>
          orders += (idAccountRef -> out)

        case "cancel_order" =>
          cancel_orders += (idAccountRef -> out)

        case "get_balance" =>
          get_balance += (idAccountRef -> out)

        case "transaction_history" =>
          transaction_history += (idAccountRef -> out)

        case "funds_history" =>
          funds_history += (idAccountRef -> out)

        case "funds" =>
          fundsList += (idAccountRef -> out)

        case "pairlist" =>
          pairlistList += (idAccountRef -> out)

        case "order_book" =>
          order_book += (idAccountRef -> out)
        }
    } catch {
      case e: Exception => println("Failed")
    }
  }
}
