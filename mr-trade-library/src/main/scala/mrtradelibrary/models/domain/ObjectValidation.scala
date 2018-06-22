package mrtradelibrary.models.domain

sealed abstract class ClassValidation

case class ObjectValidation[T](v: T) extends ClassValidation {
  def get = v
}
