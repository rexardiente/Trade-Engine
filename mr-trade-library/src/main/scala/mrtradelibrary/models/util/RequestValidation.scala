package mrtradelibrary.models.util

import javax.inject.Singleton
import java.util._
import play.api.libs.json._
import mrtradelibrary.models.domain._
import mrtradelibrary.models.util._
import mrtradelibrary.utils._

trait RequestValidation

@Singleton
object RequestValidation
    extends ValueDeserializer
    with CurrencyDeserializer
    with PaymentDeserializer
    with TradeDeserializer
    with SideDeserializer {

  implicit def jsToObject[RV >: RequestValidation, JS <: JsValue](js: JS): Option[RV] = {
    (js \ "command").as[Transaction.Value] match {
      case Transaction.balance => js.asOpt[Balance]

      case Transaction.order => js.asOpt[Order]

      case Transaction.cancelOrder => js.asOpt[CancelOrder]

      case Transaction.funds => js.asOpt[Funds]

      case Transaction.fundsHistory => js.asOpt[RequestFundsHistory]

      case Transaction.openOrders => js.asOpt[OpenOrders]

      case Transaction.getBalance => js.asOpt[GetBalance]

      case Transaction.transactionHistory => js.asOpt[TransactionHistory]

      case Transaction.pairlist => js.asOpt[PairlistRequest]

      case Transaction.orderBook => js.asOpt[AllOrders]

      case _ => None
    }
  }
}
