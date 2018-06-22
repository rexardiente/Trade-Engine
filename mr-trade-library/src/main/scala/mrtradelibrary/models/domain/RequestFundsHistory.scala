package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.models.util.{ ValueDeserializer, RequestValidation }

case class RequestFundsHistory(
    command: Transaction.Value,
    idAccountRef: UUID)
  extends RequestValidation

object RequestFundsHistory extends ValueDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[RequestFundsHistory] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID]
  )(RequestFundsHistory.apply _, unlift(RequestFundsHistory.unapply _))
}
