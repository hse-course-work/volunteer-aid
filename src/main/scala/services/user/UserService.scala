package services.user

import models.UserId
import models.dao.user.User
import models.requests.user.{AuthenticateUserRequest, SignInUserRequest}
import models.responses.UserResponse
import zio.{IO, Task}

trait UserService {

  def getUser(id: UserId): Task[User]
  def authenticate(authenticateRequest: AuthenticateUserRequest): Task[UserResponse]

  def signIn(sigInRequest: SignInUserRequest): Task[UserResponse]

}
