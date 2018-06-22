package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class InvalidRequest(command: Transaction.Value, idAccountRef: UUID) extends RequestValidation

object InvalidRequest extends ValueDeserializer {
  implicit val InvalidRequestReads: Format[InvalidRequest] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID]
  )(InvalidRequest.apply _, unlift(InvalidRequest.unapply _))
}
