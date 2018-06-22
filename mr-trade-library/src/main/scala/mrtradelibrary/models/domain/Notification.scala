package mrtradelibrary.models.domain

import akka.actor._

case class Notification(
    command: String,
    idAccountRef: java.util.UUID,
    actorRef: ActorRef,
    status: Option[Boolean])
