package mrtradelibrary.models.util

import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Trades
import mrtradelibrary.utils.Trades._

trait TradeDeserializer {
  implicit val tradeJsonFormat: Format[Value] = Format(
    Reads.of[String].map(s => Trades.withName(s)),
    Writes(a => Writes.of[String].writes(a.toString))
  )
}
