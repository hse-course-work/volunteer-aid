package repositories.user

import cats.implicits.toFoldableOps
import doobie.{ConnectionIO, Fragment, Get, Put, Read, Write}
import zio.{Task, URLayer, ZLayer}
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.util.meta.{MetaConstructors, SqlMetaInstances}
import models._
import models.dao.user.User
import models.requests.user.UpdateProfileRequest
import repositories.user.UserDaoImpl.Sql
import zio.interop.catz._

class UserDaoImpl(master: Transactor[Task]) extends UserDao {

  def getById(id: UserId): Task[Option[User]] =
    Sql.getUserById(id).transact(master)

  def getByEmail(email: Email): Task[Option[User]] =
    Sql.getUserByEmail(email).transact(master)

  def insert(user: User): Task[Unit] =
    Sql
      .insertUser(user)
      .transact(master)
      .unit

  def updateProfile(request: UpdateProfileRequest): Task[Unit] =
    Sql
      .updateInfo(request)
      .transact(master)
      .unit

  override def deleteProfile(id: UserId): Task[Unit] =
    Sql
      .delete(id)
      .transact(master)
      .unit
}

object UserDaoImpl {

  val live: URLayer[Transactor[Task], UserDao] =
    ZLayer.fromFunction(new UserDaoImpl(_))

  object Sql {

    import Implicits._

    def getUserById(id: UserId): ConnectionIO[Option[User]] =
      sql"""
            SELECT * FROM users WHERE id = $id
         """
        .query[User]
        .option

    def getUserByEmail(email: Email): ConnectionIO[Option[User]] =
      sql"""
            SELECT * FROM users WHERE emil = $email
         """
        .query[User]
        .option

    def insertUser(user: User): ConnectionIO[Int] =
      sql"""
            INSERT INTO users (
            email, hash_password, profile_description, login, photo_url
            )
            VALUES (
              ${user.email},
              ${user.hashPassword},
              ${user.profileDescription},
              ${user.login},
              ${user.photoData}
            )
         """.update.run

    def updateInfo(request: UpdateProfileRequest): ConnectionIO[Int] = {
      val baseQuery = sql""" UPDATE users SET """

      def optClause(columnName: String, valueOpt: Option[String]): Option[Fragment] =
        valueOpt.map(value => fr"$columnName = $value")

      val clauses = List(
        optClause("login", request.login),
        optClause("profile_description", request.profileDescription),
        request.photoData.map(d => fr"photo_data = $d")
      ).flatten // Убираем из списка None элементы

      val setClause = clauses.intercalate(fr",")

      val whereClause = fr"WHERE id = ${request.id}"

      val updateQuery = baseQuery ++ setClause ++ whereClause

      updateQuery.update.run
    }

    def delete(id: UserId): ConnectionIO[Int] =
      sql"DELETE FROM users WHERE id = $id"
        .update
        .run

  }

  object Implicits extends MetaConstructors with SqlMetaInstances {

    implicit val getUserId: Get[UserId] =
      Get[Long].map(id => UserId(id))

    implicit val putUserId: Put[UserId] =
      Put[Long].contramap(id => UserId.unwrap(id))

    implicit val getEmail: Get[Email] =
      Get[String].map(email => Email(email))

    implicit val putEmail: Put[Email] =
      Put[String].contramap(email => Email.unwrap(email))

    implicit val getPassword: Get[Password] =
      Get[String].map(password => Password(password))

    implicit val putPassword: Put[Password] =
      Put[String].contramap(password => Password.unwrap(password))

    implicit val getDescription: Get[Option[Description]] =
      Get[String].map(description =>
        description.isBlank match {
          case true => None
          case false => Some(Description(description))
        }
      )

    implicit val putPhotoData: Put[Option[List[Byte]]] =
      Put[Array[Byte]].contramap(bytes => bytes.map(_.toArray).getOrElse(Array.empty))

    implicit val getPhotoData: Get[Option[List[Byte]]] =
      Get[Array[Byte]].map(bytes =>
        bytes.isEmpty match {
          case true => None
          case false => Some(bytes.toList)
        }
      )

    implicit val putDescription: Put[Option[Description]] =
      Put[String].contramap(description => description.map(Description.unwrap).getOrElse(""))

    implicit val getUser: Read[User] =
      Read[(Long, String, String, Option[String], String, Option[List[Byte]])].map {
        case (id, email, password, description, login, photo) =>
          User(UserId(id), Email(email), Password(password), description.map(Description(_)), login, photo)
      }

  }

}
