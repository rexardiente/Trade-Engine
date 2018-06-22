package mrtradelibrary.utils

import mrtradelibrary.utils._
import mrtradelibrary.models.domain.Response._

object TransactionMessage {
    def failOrSuccess[A, T >: A](status: T): String =  {
      if (status == true || status == "t")
        "success"
      else
        "fail"
    }

  def description(message: String, description: String): String = {
    if (message == Transaction.cancelOrder.toString) {
      description match {
        case "success" => successCancelMessage
        case _ => failCancelMessage
      }
    } else if(message == Transaction.order.toString) {
      description match {
        case "success" => successOrderMessage
        case _ => failOrderMessage
      }
    } else if(message == Transaction.deposit.toString) {
      description match {
        case "success" => successDepositMessage
        case _ => failDepositMessage
      }
    } else if(message == Transaction.withdraw.toString) {
      description match {
        case "success" => successWithdrawMessage
        case _ => failWithdrawMessage
      }
    } else "message was not found"
  }
}
