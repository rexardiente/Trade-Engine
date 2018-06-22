package mrtradelibrary.models.domain

import java.util.UUID
import java.time.Instant
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

case class FundsHistory(
    id: Int,
    idAccountRef: UUID,
    idCurrency: Currencies.Value,
    paymentType: Payments.Value,
    amount: scala.math.BigDecimal,
    createdAt: java.time.Instant)
  extends RequestValidation

object FundsHistory extends PaymentDeserializer with CurrencyDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[FundsHistory] = (
    (__ \ "id").format[Int] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "id_currency").format[Currencies.Value] and
    (__ \ "payment_type").format[Payments.Value] and
    (__ \ "amount").format[BigDecimal] and
    (__ \ "created_at").format[Instant]
  )(FundsHistory.apply _, unlift(FundsHistory.unapply _))
}
