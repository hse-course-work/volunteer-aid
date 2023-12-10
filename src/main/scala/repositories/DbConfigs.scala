package repositories

import doobie.util.transactor.Transactor
import zio.Task
import zio.interop.catz._

object DbConfigs {

  // todo вынести в конфиг файл
  val xa = Transactor.fromDriverManager[Task](
    driver = "org.postgresql.Driver", // JDBC driver classname
    url = "jdbc:postgresql://localhost:5435/postgres", // Connect URL
    user = "postgres", // Database user name
    password = "postgres", // Database password
    logHandler = None // Don't setup logging for now. See Logging page for how to log events in detail
  )

}
