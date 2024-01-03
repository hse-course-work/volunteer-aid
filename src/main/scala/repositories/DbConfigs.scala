package repositories

import doobie.util.transactor.Transactor
import pureconfig.ConfigReader.Result
import pureconfig._
import pureconfig.generic.auto._
import zio.Task
import zio.interop.catz._

case class DbConfigs(
    driver: String,
    url: String,
    user: String,
    password: String
)
object DbConfigs {

  private val config =
    ConfigSource.resources("application.conf").at("db").loadOrThrow[DbConfigs]

  val xa = Transactor.fromDriverManager[Task](
    driver = config.driver,
    url = config.url,
    user = config.user,
    password = config.password,
    logHandler =
      None // Don't setup logging for now. See Logging page for how to log events in detail
  )

}
