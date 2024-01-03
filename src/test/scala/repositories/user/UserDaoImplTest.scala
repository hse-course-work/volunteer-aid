package repositories.user

import models.{Email, UserId}
import models._
import models.user.User
import repositories.DbConfigs
import zio.{EnvironmentTag, Scope, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.{assert, assertTrue}
import zio.test.Assertion.{anything, equalTo, fails, isUnit}

object UserDaoImplTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("UserDaoTest")(
      test1
    ).provideShared(ZLayer.succeed(new UserDaoImpl(DbConfigs.xa)))

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
