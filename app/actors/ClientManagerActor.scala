package actors

import akka.actor._
import play.api.libs.json._
import play.api.Logger

object ClientManagerActor {
  def props = Props[ClientManagerActor]
  case class Connect(connection: ActorRef)
  case class Disconnect(disconnection: ActorRef)
  case class Broadcast(connection: ActorRef)
}

class ClientManagerActor extends Actor {
  import context.dispatcher
  import ClientManagerActor._
  import mrtradelibrary.models.util.Clients

  override def preStart = {
    Logger.info("Started:" + self)
  }

  def receive = {
    case connect: Connect => // Indicators if there's new Client Connected..
      Clients.clientsList.append(connect.connection)
      self ! Json.obj("message"->"Connected")

    case disconnect: Disconnect => // Indicators if there's a Client Disconnected..
      Clients.clientsList.find(_ == disconnect.disconnection).map(Clients.clientsList -= _)
      self ! Json.obj("message"->"Somebody is disconnected")

    case json: JsValue => // Sends all the messages to all Clients..
      Clients.clientsList.map(_ ! json)
  }
}
