
// @GENERATOR:play-routes-compiler
// @SOURCE:/Volumes/Development/Projects/mr-trade-engine-2.0/conf/routes
// @DATE:Wed Apr 25 15:00:44 PHT 2018


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
