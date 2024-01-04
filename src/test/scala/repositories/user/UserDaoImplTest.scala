package repositories.user

import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import zio.interop.catz._
import models.{Email, UserId}
import models._
import models.user.User
import repositories.DbConfigs
import utils.{InitSchema, PostgresTestContainer}
import zio.{EnvironmentTag, Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.{assert, assertTrue}
import zio.test.Assertion.{anything, equalTo, fails, isUnit}
import zio.test.TestAspect.{sequential, before}

object UserDaoImplTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = {
    (suite("UserDaoTest")(
      test0,
      test1
    ) @@ before(initTable) @@ sequential)
      .provideLayer(makeLayer)
  }

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
        _ = println(result)
      } yield assertTrue(result == "users")
    }
  }

  def test1 = {
    test("test1") {
      val testUser = User(
        UserId(1),
        Email("example@mail.ru"),
        Password("pssword"),
        Description("descripton")
      )
      for {
        dao <- ZIO.service[UserDao]
        user <- dao.get(UserId(0))
      } yield assertTrue(user == testUser)
    }
  }
}
