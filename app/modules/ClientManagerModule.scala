package modules

class ClientManagerModule
  extends com.google.inject.AbstractModule
  with play.api.libs.concurrent.AkkaGuiceSupport {
  protected def configure() = {
    bindActor[actors.ClientManagerActor]("ClientManagerActor")
  }
}
