package actors

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits._
import akka.actor._
import akka.pattern.ask
import play.api.Logger
import play.api.libs.json._
import mrtradelibrary.models.domain.{ AllOrders, PairlistRequest }
import mrtradelibrary.models.util._

object AccountManagerActor {
  def props(
    out: ActorRef,
    accountServiceActor: ActorRef,
    clientManagerActor: ActorRef) =
    Props(classOf[AccountManagerActor], out, accountServiceActor, clientManagerActor)
}

@Singleton
class AccountManagerActor(
    out: ActorRef,
    accountServiceActor: ActorRef,
    clientManagerActor: ActorRef) extends Actor with CacheUsers {
  import ClientManagerActor._

  override def preStart() = {
    super.preStart
    clientManagerActor ! Connect(out)
  }

  // Triggered if the connection's disconnected from client.
  override def postStop() = {
    clientManagerActor ! Disconnect(out)
  }

  def requestValidation(request: Option[RequestValidation]) =
    request match {
      case Some(allOrders: AllOrders) =>
        accountServiceActor ! allOrders

      case Some(pairlist: PairlistRequest) =>
        accountServiceActor ! pairlist

      case other =>
        println("Invalid request.")
    }

  override def receive: Receive = {
    case json: JsValue =>
      try {
        saveUser(json, out)
      } finally {
        requestValidation(json)
      }

    case other =>
      println(s"Received : $other")
  }
}
