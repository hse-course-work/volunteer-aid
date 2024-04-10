package repositories

import doobie.util.transactor.Transactor
import utils.{InitSchema, PostgresTestContainer}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test.{assertTrue, assertZIO, Spec, TestEnvironment, ZIOSpecDefault}
import doobie.implicits._
import models.dao.hashtag.Hashtag
import models.dao.hashtag.Hashtag.Tag
import repositories.hashtags.{HashtagDao, HashtagDaoImpl}
import zio.interop.catz._
import zio.test.Assertion.isUnit
import zio.test.TestAspect.{after, before, sequential}

object HashtagDaoIpmTest extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment with Scope, Any] =
    (suite("HashtagDaoIpm")(
      creatingTable,
      addTag,
      deleteTag
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

  def creatingTable = {
    test("check table exists") {
      for {
        xa <- ZIO.service[Transactor[Task]]
        result <- sql"""
                       SELECT table_name
                       FROM information_schema.tables
                       WHERE table_schema = 'public' AND table_name LIKE 'task_hashtags';
                  """
          .query[String]
          .unique
          .transact(xa)
      } yield assertTrue(result == "task_hashtags")
    }
  }

  def addTag = {
    test("successful add hashtag to task") {
      assertZIO(
        ZIO.serviceWithZIO[HashtagDao](
          _.addHashtag(
            Hashtag(Tag.Animal, 1)
          )
        )
      )(isUnit)
    }
  }

  def deleteTag = {
    test("successful delete hashtag to task") {
      assertZIO(
        ZIO.serviceWithZIO[HashtagDao](
          _.deleteHashtag(
            Hashtag(Tag.Animal, 1)
          )
        )
      )(isUnit)
    }
  }

}
