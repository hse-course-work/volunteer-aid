package services.user

import models.{Description, Email, Password, UserId}
import models.dao.user.User
import models.requests.user.{AuthenticateUserRequest, SignInUserRequest}
import models.responses.UserResponse
import repositories.user.UserDao
import services.user.UserService.UserException
import services.user.UserService.UserException._
import services.user.UserServiceImpl.defaultUser
import utils.PasswordHelper
import zio.{IO, Task, URLayer, ZIO, ZLayer}

class UserServiceImpl(dao: UserDao) extends UserService {

  def getUser(id: UserId): Task[User] =
    dao.getById(id).map(_.getOrElse(defaultUser))

  def authenticate(authenticateRequest: AuthenticateUserRequest): IO[UserException, UserResponse] =
    for {
      probablyUser <- dao
        .getByEmail(Email(authenticateRequest.email))
        .catchAll(e => ZIO.fail(InternalError(e)))
      response <- probablyUser match {
        case Some(user) => checkPassword(authenticateRequest.password, user)
        case None => ZIO.fail(UserNotFound(authenticateRequest.email))
      }
    } yield response

  private def checkPassword(inputPassword: String, user: User): IO[UserException, UserResponse] =
    for {
      password <- ZIO
        .attempt(PasswordHelper.decode(Password.unwrap(user.hashPassword)))
        .catchAll(e => ZIO.fail(InternalError(e)))
      userResponse <-
        if (inputPassword == password)
          ZIO.succeed(UserResponse.convert(user))
        else ZIO.fail(BadEmailOrPassword())
    } yield userResponse

  def signIn(sigInRequest: SignInUserRequest): Task[UserResponse] =
    ZIO.succeed(UserResponse(0, "", "", "", None))
}

object UserServiceImpl {

  val live: URLayer[UserDao, UserService] =
    ZLayer.fromFunction(new UserServiceImpl(_))

  private val defaultUser: User =
    User(
      UserId(1),
      Email("example@mail.ru"),
      Password("pssword"),
      Description("descripton"),
      "default_login",
      None
    )

}
