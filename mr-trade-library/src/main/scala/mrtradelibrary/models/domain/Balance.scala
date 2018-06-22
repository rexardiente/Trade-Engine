package mrtradelibrary.models.domain

import java.util.UUID
import java.time.Instant
import scala.math.BigDecimal._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class Balance(
    id: Int,
    idAccountRef: UUID,
    idCurrency: Currencies.Value,
    balance: BigDecimal,
    createdAt: Instant)
  extends RequestValidation

object Balance extends CurrencyDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Balance] = (
    (__ \ "id").format[Int] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "id_currency").format[Currencies.Value] and
    (__ \ "balance").format[BigDecimal] and
    (__ \ "created_at").format[Instant]
  )(Balance.apply _, unlift(Balance.unapply _))
}
