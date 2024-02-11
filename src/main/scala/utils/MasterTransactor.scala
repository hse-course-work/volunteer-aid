package utils

import repositories.DbConfigs
import doobie.util.transactor.Transactor
import zio.{Task, URLayer, ZLayer}
import zio.interop.catz._

class MasterTransactor(dbConfigs: DbConfigs) {

  def transactor: Transactor[Task] =
    Transactor.fromDriverManager[Task](
      driver = dbConfigs.driver,
      url = dbConfigs.url,
      user = dbConfigs.user,
      password = dbConfigs.password,
      logHandler =
        None // Don't setup logging for now. See Logging page for how to log events in detail
    )
}

object MasterTransactor {

  val live: URLayer[DbConfigs, Transactor[Task]] =
    ZLayer.fromFunction {
      (cnf: DbConfigs) =>
        val transactor = new MasterTransactor(cnf)
        transactor.transactor
    }

}

