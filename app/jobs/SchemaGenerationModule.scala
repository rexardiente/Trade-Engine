package jobs

class SchemaGenerationModule extends com.google.inject.AbstractModule {
  protected def configure() = {
    bind(classOf[mrtradelibrary.models.util.SchemaGenerator]).asEagerSingleton()
  }
}
