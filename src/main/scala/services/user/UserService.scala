package services.user

import models.UserId
import models.dao.user.User
import models.requests.user.{AuthenticateUserRequest, SignInUserRequest}
import models.responses.UserResponse
import services.user.UserService.UserException
import zio.{IO, Task}

trait UserService {

  def getUser(id: UserId): Task[User]
  def authenticate(authenticateRequest: AuthenticateUserRequest): IO[UserException, UserResponse]

  def signIn(sigInRequest: SignInUserRequest): IO[UserException, UserResponse]

}

object UserService {
  sealed trait UserException {
    def msg: String
  }

  object UserException {

    case class ProfileWithEmailAlreadyExist(email: String) extends UserException {
      def msg: String = s"Account with email: $email has already exist!"
    }

    case class UserNotFound(email: String) extends UserException {
      def msg: String = s"$email Not Found"
    }

    case class BadEmailOrPassword(prompt: Option[String]) extends UserException {
      def msg: String = s"Incorrect email or password. ${prompt.getOrElse("")}"
    }

    case class InternalError(e: Throwable) extends UserException {
      def msg: String = s"Something went wrong, ${e.getMessage}"
    }
  }
}