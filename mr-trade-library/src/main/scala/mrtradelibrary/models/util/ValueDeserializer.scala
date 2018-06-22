package mrtradelibrary.models.util

import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Transaction
import mrtradelibrary.utils.Transaction._

trait ValueDeserializer {
  implicit val transactionJsonFormat: Format[Value] = Format(
    Reads.of[String].map { TJF =>
      scala.util.control.Exception.catching(classOf[NoSuchElementException])
        .opt(Transaction.withName(TJF))
        .getOrElse(Transaction.invalidTransaction)
    },
    Writes(TJF => Writes.of[String].writes(TJF.toString))
  )
}
