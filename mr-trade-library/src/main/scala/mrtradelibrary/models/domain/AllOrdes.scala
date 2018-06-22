package mrtradelibrary.models.domain

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.models.util.{ ValueDeserializer, CurrencyDeserializer, RequestValidation }
import mrtradelibrary.utils._

case class AllOrders(
  command: Transaction.Value,
  idAccountRef: UUID,
  currencyBase: Currencies.Value,
  currencyCounter: Currencies.Value,
  levels: Option[Int]) extends RequestValidation {
  def toJson = Json.toJson(this)
}

object AllOrders extends ValueDeserializer with CurrencyDeserializer {
  implicit val AllOrderBookFormats: Format[AllOrders] =  {
    Format(json => JsSuccess(apply(
      (json\ "command").as[Transaction.Value],
      (json\ "id_account_ref").as[UUID],
      (json\ "currency_base").as[Currencies.Value],
      (json\ "currency_counter").as[Currencies.Value],
      (json\ "levels").asOpt[Int]
    )), (order: AllOrders) => Json.toJson(order))
  }
}
