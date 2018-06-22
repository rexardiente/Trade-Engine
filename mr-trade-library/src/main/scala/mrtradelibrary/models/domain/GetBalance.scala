package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.models.util._

case class GetBalance (
  command: Transaction.Value,
  idAccountRef: UUID
) extends RequestValidation

object GetBalance extends ValueDeserializer  {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[GetBalance] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID]
  )(GetBalance.apply _, unlift(GetBalance.unapply _))
}
