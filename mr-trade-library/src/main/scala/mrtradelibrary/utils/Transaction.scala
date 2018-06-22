package mrtradelibrary.utils

object Transaction extends Enumeration {
  type Role = Value

  val order = Value("order")
  val cancelOrder = Value("cancel_order")
  val funds = Value("funds")
  val fundsHistory = Value("funds_history")
  val withdraw = Value("withdraw")
  val deposit = Value("deposit")
  val orderResponse = Value("order_response")
  val balance = Value("balance")
  val getBalance = Value("get_balance")
  val openOrders = Value("open_orders")
  val invalidTransaction = Value("invalid_transaction")
  val transactionHistory = Value("transaction_history")
  val pairlist = Value("pairlist")
  val orderBook = Value("order_book")
}

