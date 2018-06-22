package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils._
import mrtradelibrary.models.util._

case class Funds(
    command: Transaction.Value,
    idAccountRef: UUID,
    amount: BigDecimal,
    codeCurrency: Currencies.Value,
    isDeposit: Boolean)
  extends mrtradelibrary.models.util.RequestValidation

object Funds extends ValueDeserializer with CurrencyDeserializer {
  implicit val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Funds] = (
    (__ \ "command").format[Transaction.Value] and
    (__ \ "id_account_ref").format[UUID] and
    (__ \ "amount").format[BigDecimal] and
    (__ \ "id_currency").format[Currencies.Value] and
    (__ \ "is_deposit").format[Boolean]
  )(Funds.apply _, unlift(Funds.unapply _))
}
