package modules

class TradeEngineAccessModule extends
  com.google.inject.AbstractModule
  with play.api.libs.concurrent.AkkaGuiceSupport {
  protected def configure() = {
    bindActor[actors.AccountServiceActor]("AccountServiceActor")
    bindActor[actors.TradeEngineAccessor]("TradeEngineAccessor")
  }
}
