package mrtrade

import play.api.libs.json._
import utils.TextConverter

object OrderCode extends Enumeration {
  type OrderCode = Value
  val Error, CREATE, CANCEL, CANCELLED, FILL, PARTIALLY_FILL = Value

  implicit val orderCodeFormat = new Format[OrderCode] {
    def reads(json: JsValue) = ??? //TODO FIX THIS LATER! - Do we need this?
    def writes(code: OrderCode) = JsString(TextConverter.camelToSnake(code.toString))
  }
}
