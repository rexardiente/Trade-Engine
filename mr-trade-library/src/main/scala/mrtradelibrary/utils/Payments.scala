package mrtradelibrary.utils

object Payments extends Enumeration {
  type Payment = Value

  val Deposit = Value("DEPOSIT")
  val Withdraw = Value("WITHDRAW")

  // LowerCase Types...
  val withdraw = Value("withdraw")
  val deposit = Value("deposit")
}

