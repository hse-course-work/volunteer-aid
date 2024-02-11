package services.user
import models.UserId
import models.dao.user.User
import models.requests.user.{AuthenticateUserRequest, SignInUserRequest}
import models.responses.UserResponse
import repositories.user.UserDao
import zio.{Task, URLayer, ZIO, ZLayer}

class UserServiceImpl(dao: UserDao) extends UserService {

  def getUser(id: UserId): Task[User] =
    dao.get(id)

  def authenticate(authenticateRequest: AuthenticateUserRequest): Task[UserResponse] =
    ZIO.succeed(UserResponse(0, "", "", "", None))

  def signIn(sigInRequest: SignInUserRequest): Task[UserResponse] =
    ZIO.succeed(UserResponse(0, "", "", "", None))
}

object UserServiceImpl {

  val live: URLayer[UserDao, UserService] =
    ZLayer.fromFunction(new UserServiceImpl(_))

}
