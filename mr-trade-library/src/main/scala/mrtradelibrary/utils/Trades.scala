package mrtradelibrary.utils

object Trades extends Enumeration {
  type Trade = Value

  val Create = Value("CREATE")
  val Cancel = Value("CANCEL")
  val Cancelled = Value("CANCELLED")
  val Fill = Value("FILL")
  val Partially_Fill = Value("PARTIALLY_FILL")
}
