package repositories

import doobie.util.transactor.Transactor
import utils.{InitSchema, PostgresTestContainer}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}
import doobie.implicits._
import models.dao.rating.Like
import org.joda.time.DateTime
import repositories.rating.{LikeDao, LikeDaoImpl}
import zio.interop.catz._
import zio.test.Assertion.{equalTo, isUnit}
import zio.test.TestAspect.{after, before, sequential}
object LikeDaoImplTest extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment with Scope, Any] =
    (suite("LikeDaoImpl")(
      creatingTable,
      addLike,
      deleteLikeForUser,
      getUserLikeById
    ) @@ before(initTable) @@ after(cleanTable) @@ sequential)
      .provideLayer(makeLayer)

  private def cleanTable =
    for {
      xa <- ZIO.service[Transactor[Task]]
      _ <- sql" DELETE FROM likes ".update.run.transact(xa)
    } yield ()

  def initTable =
    ZIO.serviceWithZIO[Transactor[Task]] { xa =>
      for {
        _ <- InitSchema("/like.sql", xa)
        _ = println("схема готова!")
      } yield ()
    }

  def makeLayer = ZLayer.make[LikeDao with Transactor[Task]](
    PostgresTestContainer.defaultSettings,
    PostgresTestContainer.postgresTestContainer,
    PostgresTestContainer.xa,
    LikeDaoImpl.live
  )

  def creatingTable = {
    test("check table exists") {
      for {
        xa <- ZIO.service[Transactor[Task]]
        result <- sql"""
                       SELECT table_name
                       FROM information_schema.tables
                       WHERE table_schema = 'public' AND table_name LIKE 'likes';
                  """
          .query[String]
          .unique
          .transact(xa)
      } yield assertTrue(result == "likes")
    }
  }

  def addLike = {
    test("successful add like to user for like") {
      assertZIO(
        ZIO.serviceWithZIO[LikeDao](
          _.createLike(
            Like(1, 1, 1, DateTime.parse("2024-04-01"))
          )
        )
      )(isUnit)
    }
  }

  def deleteLikeForUser = {
    test("successful delete like from user by task") {
      assertZIO(
        ZIO.serviceWithZIO[LikeDao](
          _.deleteLike(
            1
          )
        )
      )(isUnit)
    }
  }

  def getUserLikeById = {
    test("successful get likes for user by task") {
      assertZIO(
        ZIO.serviceWithZIO[LikeDao](
          _.get(
            userId = 1, taskId = 1
          )
        )
      )(
        equalTo(
          Some(Like(1, 1, 1, DateTime.parse("2024-04-01")))
        )
      )
    }
  }

}
