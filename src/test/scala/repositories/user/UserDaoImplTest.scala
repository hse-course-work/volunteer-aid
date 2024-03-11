package repositories.user

import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import zio.interop.catz._
import models.{Email, UserId}
import models._
import models.dao.user.User
import repositories.DbConfigs
import utils.{InitSchema, PostgresTestContainer}
import zio.{EnvironmentTag, Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.{assert, assertTrue}
import zio.test.Assertion.{anything, equalTo, fails, isUnit}
import zio.test.TestAspect.{after, before, sequential}

object UserDaoImplTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = {
    (suite("UserDaoTest")(
      test0,
      test1
    ) @@ before(initTable) @@ after(cleanTable) @@ sequential)
      .provideLayer(makeLayer)
  }

  private def cleanTable =
    for {
      xa <- ZIO.service[Transactor[Task]]
      _ <- sql" DELETE FROM users ".update.run.transact(xa)
    } yield ()

  def initTable =
    ZIO.serviceWithZIO[Transactor[Task]] { xa =>
      for {
        _ <- InitSchema("/user.sql", xa)
        _ = println("схема готова!")
      } yield ()
    }

  def makeLayer = ZLayer.make[UserDao with Transactor[Task]](
    PostgresTestContainer.defaultSettings,
    PostgresTestContainer.postgresTestContainer,
    PostgresTestContainer.xa,
    UserDaoImpl.live
  )

  def test0 = {
    test("check table exists") {
      for {
        xa <- ZIO.service[Transactor[Task]]
        result <- sql"""
                       SELECT table_name
                       FROM information_schema.tables
                       WHERE table_schema = 'public' AND table_name LIKE 'users';
                  """
          .query[String]
          .unique
          .transact(xa)
      } yield assertTrue(result == "users")
    }
  }

  def test1 = {
    test("successful insert and get user") {
      val testUser = User(
        email = Email("example@mail.ru"),
        hashPassword = Password("pssword"),
        profileDescription = Some(Description("descripton")),
        login = "login",
        photoData = None
      )
      for {
        dao <- ZIO.service[UserDao]
        _ <- dao.insert(testUser)
        user <- dao.getById(UserId(1))
      } yield assertTrue(user.get == testUser.copy(id = UserId(1)))
    }
  }
}
