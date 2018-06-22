package mrtradelibrary.models.domain

import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.models.util.CurrencyDeserializer
import mrtradelibrary.utils._

case class CurrencyNetwork(id: Currencies.Value, name: String)

object CurrencyNetwork extends CurrencyDeserializer  {
  val tupled = (apply _).tupled

  implicit val readAndWriteFormater: Format[CurrencyNetwork] = (
    (__ \ "id").format[Currencies.Value] and
    (__ \ "name").format[String]
  )(CurrencyNetwork.apply _, unlift(CurrencyNetwork.unapply _))
}
