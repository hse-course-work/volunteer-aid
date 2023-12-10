package repositories.user

import doobie.{ConnectionIO, Get, Put, Read}
import models.user.User
import zio.Task
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.util.meta.{MetaConstructors, SqlMetaInstances}
import models._
import repositories.user.UserDaoImpl.Sql
import zio.interop.catz._

class UserDaoImpl(master: Transactor[Task]) extends UserDao {

  override def get(id: UserId): Task[User] =
    Sql.getUserById(id).transact(master)
      .map(user =>
        user.getOrElse(
          User(
            UserId(1),
            Email("example@mail.ru"),
            Password("pssword"),
            Description("descripton")
          )
        )
      )

}

object UserDaoImpl {

  object Sql {
    import Implicits._

    def getUserById(id: UserId): ConnectionIO[Option[User]] =
      sql"""
            SELECT * FROM users WHERE id = $id
         """
        .query[User]
        .option

  }
  object Implicits extends MetaConstructors with SqlMetaInstances{

    implicit val getUserId: Get[UserId] =
      Get[Long].map(id => UserId(id))

    implicit val putUserId: Put[UserId] =
      Put[Long].contramap(id => UserId.unwrap(id))

    implicit val getUser: Read[User] =
      Read[(Long, String, String, String)].map {
        case (id, email, password, desc) => User(UserId(id), Email(email), Password(password), Description(desc))
      }

  }

}