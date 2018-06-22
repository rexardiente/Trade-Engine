package mrtradelibrary.models.util

import java.util.UUID
import scala.collection.mutable.HashMap
import akka.actor._

object Clients {
  val clientsList = scala.collection.mutable.ArrayBuffer[ActorRef]()
  val open_orders = HashMap.empty[UUID, ActorRef]
  val orders = HashMap.empty[UUID, ActorRef]
  val cancel_orders = HashMap.empty[UUID, ActorRef]
  val get_balance = HashMap.empty[UUID, ActorRef]
  val transaction_history = HashMap.empty[UUID, ActorRef]
  val funds_history = HashMap.empty[UUID, ActorRef]
  val fundsList = HashMap.empty[UUID, ActorRef]
  val pairlistList = HashMap.empty[UUID, ActorRef]
  val order_book = HashMap.empty[UUID, ActorRef]
}
