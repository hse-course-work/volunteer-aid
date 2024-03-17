package repositories

import doobie.implicits._
import doobie.util.transactor.Transactor
import models.dao.user.User
import models.requests.user.UpdateProfileRequest
import models._
import repositories.user.{UserDao, UserDaoImpl}
import utils.{InitSchema, PostgresTestContainer}
import zio.interop.catz._
import zio.test.Assertion.isUnit
import zio.test.TestAspect.{after, before, sequential}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}
import zio.{Scope, Task, ZIO, ZLayer}

object UserDaoImplTest extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = {
    (suite("UserDaoTest")(
      creatingTable,
      successfulInsertUser,
      successfulInsertUser,
      successfulUpdateUserProfile
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

  def creatingTable = {
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

  def successfulInsertUser = {
    test("successful insert user") {
      val testUser = User(
        email = Email("example@mail.ru"),
        hashPassword = Password("pssword"),
        profileDescription = Some(Description("descripton")),
        login = "login",
        photoData = None
      )
      val result = (for {
        dao <- ZIO.service[UserDao]
        _ <- dao.insert(testUser)
      } yield ())

      assertZIO(result)(isUnit)
    }
  }

  def successfulGetUserByEmail = {
   test("successful get user by id") {
     for {
       dao <- ZIO.service[UserDao]
       xa <- ZIO.service[Transactor[Task]]
       _ <- Help.fillBase(xa)
       user <- dao.getByEmail(Email("example1@mail.ru"))
     } yield assertTrue(user.get == Help.defaultUsers.head.copy(id = UserId(0)))
   }
  }

  def successfulUpdateUserProfile = {
    test("successful update user profile - new login") {
      for {
        dao <- ZIO.service[UserDao]
        xa <- ZIO.service[Transactor[Task]]
        _ <- Help.fillBase(xa)
        testUser <- dao.getByEmail(Email("example1@mail.ru"))
        u = testUser.get
        _ <- dao.updateProfile(UpdateProfileRequest(UserId.unwrap(u.id), None, login = Some("new_login"), None))
        newUser <- dao.getByEmail(Email("example1@mail.ru"))
      } yield assertTrue(newUser.get.login == "new_login")
    }
  }

  def successfulDeleteUser = {
    test("successful delete user profile") {
      for {
        dao <- ZIO.service[UserDao]
        xa <- ZIO.service[Transactor[Task]]
        _ <- Help.fillBase(xa)
        testUser <- dao.getByEmail(Email("example1@mail.ru"))
        u = testUser.get
        _ <- dao.deleteProfile(u.id)
        newUser <- dao.getByEmail(Email("example1@mail.ru"))
      } yield assertTrue(newUser.isEmpty)
    }
  }


  object Help {

    def fillBase(xa: Transactor[Task], users: Seq[User] = defaultUsers): Task[Unit] =
      ZIO.foreachDiscard(users) { user =>
        insertUser(user).transact(xa)
      }

    val defaultUsers =
      Seq(
        User(Email("example1@mail.ru"), Password("pssword1"), Some(Description("descripton1")), "login1", None),
        User(Email("example2@mail.ru"), Password("pssword2"), Some(Description("descripton2")), "login2", None),
        User(Email("example3@mail.ru"), Password("pssword3"), Some(Description("descripton3")), "login3", None),
        User(Email("example4@mail.ru"), Password("pssword4"), Some(Description("descripton4")), "login4", None),
      )

    private def insertUser(user: User) =
      sql"""
            INSERT INTO users (
            email, hash_password, description, login, photo_data
            )
            VALUES (
              ${Email.unwrap(user.email)},
              ${Password.unwrap(user.hashPassword)},
              ${Description.unwrap(user.profileDescription.get)},
              ${user.login},
              ${user.photoData}
            )
         """.update.run
  }
}
