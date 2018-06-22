package mrtradelibrary.models.util

import play.api.libs.json._
import play.api.libs.functional.syntax._
import mrtradelibrary.utils.Sides
import mrtradelibrary.utils.Sides._

trait SideDeserializer {
  implicit val sideJsonFormat: Format[Value] = Format(
    Reads.of[String].map(s => Sides.withName(s)),
    Writes(a => Writes.of[String].writes(a.toString))
  )
}
