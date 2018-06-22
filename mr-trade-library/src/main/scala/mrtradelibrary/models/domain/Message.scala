package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._
import mrtradelibrary.models.domain._


case class Message(message: String, response: Response)

object Message extends TradeDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Message] = (
    (__ \ "message").format[String] and
    (__ \ "response").format[Response]
  )(Message.apply _, unlift(Message.unapply _))
}


case class Response(status: String, description: String)

object Response {
  val successCancelMessage = "Order has been successfully cancelled"
  val failCancelMessage = "Order was not cancelled"
  val successOrderMessage = "Order has been successfully placed"
  val failOrderMessage = "Order was not placed"
  val successWithdrawMessage = "Withdraw successfully"
  val failWithdrawMessage = "Withdrawal failed"
  val successDepositMessage = "Deposit successfully"
  val failDepositMessage = "Deposit failed"

  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Response] = (
    (__ \ "status").format[String] and
    (__ \ "description").format[String]
  )(Response.apply _, unlift(Response.unapply _))

}

case class MessagePairlist(message: String, response: List[PairlistSetResult])

object MessagePairlist {

  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[MessagePairlist] = (
    (__ \ "message").format[String] and
    (__ \ "response").format[List[PairlistSetResult]]
  )(MessagePairlist.apply _, unlift(MessagePairlist.unapply _))
}

case class MessageResponse[+A, +T](message: T, response: Seq[A]) {
  def toJson(implicit aa: Writes[A], bb: Writes[T]) = Json.obj("message"-> message.toString, "response" -> response)
}
