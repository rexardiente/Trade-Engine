package mrtradelibrary.utils

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits._
import collection.breakOut

object ConvertStringToObject {
  def apply[A, T <: String, O >: A] (tt: T, obj: O): O = {
    val filtered: List[String] = tt.filterNot("()".toSet)
        .replaceAll(",", ','.toString())
        .split(",").map(_.trim)(breakOut)

    ConvertToObject(filtered , obj).asInstanceOf[O]
  }

  private def ConvertToObject[A, T, O >: A, B >: T] (list: List[B], obj: O): O = {
    obj.getClass
      .getMethods
      .find(x => x.getName == "apply" && x.isBridge)
      .get
      .invoke(obj, list.map(_.asInstanceOf[AnyRef]): _*)
      .asInstanceOf[O]
  }
}
