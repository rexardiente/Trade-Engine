package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.models.util.{ ValueDeserializer, RequestValidation }

case class TransactionHistory(
    command: Transaction.Value,
    idAccountRef: UUID)
  extends RequestValidation

object TransactionHistory extends ValueDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[TransactionHistory] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID]
  )(TransactionHistory.apply _, unlift(TransactionHistory.unapply _))
}
