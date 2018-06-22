package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._
import mrtradelibrary.models.domain._

case class Pairlist(currencyBase: Currencies.Value, currencyCounter: Currencies.Value)
object Pairlist extends CurrencyDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[Pairlist] = (
    (__ \ "code_currency_base").format[Currencies.Value] and
    (__ \ "code_currency_counter").format[Currencies.Value]
  )(Pairlist.apply _, unlift(Pairlist.unapply _))
}

case class PairlistSet(pairlist: Seq[Pairlist] = Seq.empty[Pairlist])
object PairlistSet {
  implicit val pairlistSetFormats: Format[PairlistSet] =  {
    Format(json => JsSuccess(apply((json\ "set").as[Seq[Pairlist]])),
      (set: PairlistSet) => Json.toJson(set.pairlist))
  }
}

case class PairlistRequest(command: Transaction.Value, idAccountRef: UUID) extends RequestValidation
object PairlistRequest extends ValueDeserializer {
  implicit val pairlistFormats: Format[PairlistRequest] =  {
    Format(json => JsSuccess(apply(
      (json\ "command").as[Transaction.Value],
      (json\ "id_account_ref").as[UUID])),
      (set: PairlistRequest) => Json.toJson(set.command))
  }
}

case class PairlistSetResult(
    currencyBase: Currencies.Value,
    currencyCounter: Currencies.Value,
    latestPrice: BigDecimal,
    volume24hr: BigDecimal,
    change24hr: BigDecimal)

object PairlistSetResult extends CurrencyDeserializer {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[PairlistSetResult] = (
    (__ \ "currency_base").format[Currencies.Value] and
    (__ \ "currency_counter").format[Currencies.Value] and
    (__ \ "latest_price").format[BigDecimal] and
    (__ \ "volume_24hr").format[BigDecimal] and
    (__ \ "change_24hr").format[BigDecimal]
  )(PairlistSetResult.apply _, unlift(PairlistSetResult.unapply _))
}
