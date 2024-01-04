package utils

import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import io.github.scottweaver.models.JdbcInfo
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio.{Task, ZIO, ZLayer}
import zio.interop.catz._


object PostgresTestContainer {

  val postgresTestContainer = ZPostgreSQLContainer.live
  val defaultSettings = ZPostgreSQLContainer.Settings.default

  val xa = ZLayer.fromZIO {
    ZIO.serviceWithZIO[JdbcInfo] { jdbcInfo =>
      ZIO.attempt(
        Transactor.fromDriverManager[Task](
          driver = jdbcInfo.driverClassName,
          url = jdbcInfo.jdbcUrl,
          user = jdbcInfo.username,
          password = jdbcInfo.password,
          logHandler = None
        )
      )
    }
  }
}
