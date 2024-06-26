package repositories

import doobie.util.transactor.Transactor
import utils.{InitSchema, PostgresTestContainer}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}
import doobie.implicits._
import models.dao.push.Push
import org.joda.time.DateTime
import repositories.push.{PushDao, PushDaoImpl}
import zio.interop.catz._
import zio.test.Assertion.{equalTo, isUnit}
import zio.test.TestAspect.{after, before, sequential}
object PushDaoImplTest extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment with Scope, Any] =
    (suite("PushDaoImpl")(
      creatingTable,
      addPush,
      getUserPushes
    ) @@ before(initTable) @@ after(cleanTable) @@ sequential)
      .provideLayer(makeLayer)

  private def cleanTable =
    for {
      xa <- ZIO.service[Transactor[Task]]
      _ <- sql" DELETE FROM pushes ".update.run.transact(xa)
    } yield ()

  def initTable =
    ZIO.serviceWithZIO[Transactor[Task]] { xa =>
      for {
        _ <- InitSchema("/push.sql", xa)
        _ = println("схема готова!")
      } yield ()
    }

  def makeLayer = ZLayer.make[PushDao with Transactor[Task]](
    PostgresTestContainer.defaultSettings,
    PostgresTestContainer.postgresTestContainer,
    PostgresTestContainer.xa,
    PushDaoImpl.live
  )

  def creatingTable = {
    test("check table exists") {
      for {
        xa <- ZIO.service[Transactor[Task]]
        result <- sql"""
                       SELECT table_name
                       FROM information_schema.tables
                       WHERE table_schema = 'public' AND table_name LIKE 'pushes';
                  """
          .query[String]
          .unique
          .transact(xa)
      } yield assertTrue(result == "pushes")
    }
  }

  def addPush = {
    test("successful add push to user with message") {
      assertZIO(
        ZIO.serviceWithZIO[PushDao](
          _.addPushToUser(
            Push(1, 1, 1, "test", DateTime.parse("2024-04-01"))
          )
        )
      )(isUnit)
    }
  }

  def getUserPushes = {
    test("successful get user pushes by id") {
      assertZIO(
        ZIO.serviceWithZIO[PushDao](
          _.getUserPushes(
            userId = 1
          )
        )
      )(
        equalTo(
          Seq(Push(1, 1, 1, "test", DateTime.parse("2024-04-01")))
        )
      )
    }
  }

}
