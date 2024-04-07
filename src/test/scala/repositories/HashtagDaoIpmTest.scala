package repositories

import doobie.util.transactor.Transactor
import utils.{InitSchema, PostgresTestContainer}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import doobie.implicits._
import repositories.hashtags.{HashtagDao, HashtagDaoImpl}
import zio.interop.catz._
import zio.test.TestAspect.{after, before, sequential}

object HashtagDaoIpmTest extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment with Scope, Any] =
    (suite("")(

    ) @@ before(initTable) @@ after(cleanTable) @@ sequential)
      .provideLayer(makeLayer)

  private def cleanTable =
    for {
      xa <- ZIO.service[Transactor[Task]]
      _ <- sql" DELETE FROM task_hashtags ".update.run.transact(xa)
    } yield ()

  def initTable =
    ZIO.serviceWithZIO[Transactor[Task]] { xa =>
      for {
        _ <- InitSchema("/hashtags.sql", xa)
        _ = println("схема готова!")
      } yield ()
    }

  def makeLayer = ZLayer.make[HashtagDao with Transactor[Task]](
    PostgresTestContainer.defaultSettings,
    PostgresTestContainer.postgresTestContainer,
    PostgresTestContainer.xa,
    HashtagDaoImpl.live
  )

}
