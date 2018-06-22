package utils

object TextConverter {
  def camelToSnake(text: String) = {
    text.drop(1).foldLeft(text.headOption.map(_.toLower + "") getOrElse "") {
      case (acc, c) if c.isUpper => acc + "_" + c.toLower
      case (acc, c) => acc + c
    }
  }
}