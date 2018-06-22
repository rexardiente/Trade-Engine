package mrtradelibrary.models.util

import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Payments
import mrtradelibrary.utils.Payments._

trait PaymentDeserializer {
  implicit val paymentJsonFormat: Format[Value] = Format(
    Reads.of[String].map(s => Payments.withName(s)),
    Writes(a => Writes.of[String].writes(a.toString))
  )
}
