package mrtrade

import play.api.libs.json._

case class OrderResultSet (results: List[OrderResult] = List()) {
  def toOutputJson(pair: (String, String)) = {
    results.map(result => result.toOutputJson(pair))
  }
}

object OrderResultSet {
  implicit val orderResultSetFormat = Json.format[OrderResultSet]
}