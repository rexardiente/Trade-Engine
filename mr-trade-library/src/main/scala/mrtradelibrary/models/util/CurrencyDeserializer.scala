package mrtradelibrary.models.util

import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Currencies
import mrtradelibrary.utils.Currencies._

trait CurrencyDeserializer {
  implicit val currencyJsonFormat: Format[Value] = Format(
    Reads.of[String].map(s => Currencies.withName(s)),
    Writes(a => Writes.of[String].writes(a.toString))
  )
}
