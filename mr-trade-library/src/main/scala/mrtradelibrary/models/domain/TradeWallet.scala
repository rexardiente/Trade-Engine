package mrtradelibrary.models.domain

import java.util.UUID
import java.time.Instant
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class TradeWallet(
    id: UUID,
    idAccountRef: UUID,
    color: String,
    createdAt: Instant)
  extends mrtradelibrary.models.util.RequestValidation

object TradeWallet {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[TradeWallet] = (
    (__ \ "id").format[UUID] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "color").format[String] and
    (__ \ "created_at").format[Instant]
  )(TradeWallet.apply _, unlift(TradeWallet.unapply _))
}
