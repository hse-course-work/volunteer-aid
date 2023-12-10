package api.user

import models.{Email, UserId}
import models.responses.GetUserResponse
import services.user.UserService
import sttp.tapir.ztapir._
import zio.{Task, ULayer, URLayer, ZIO, ZLayer}

class UserRouter(userService: UserService) extends UserApi {

  def getUser: ZServerEndpoint[Any, Any] =
    get.zServerLogic(id =>
      userService.getUser(UserId(id))
        .map(user => GetUserResponse(s"user: ${user.id}", Email.unwrap(user.email)))
        .catchAll(e => ZIO.fail(e.getMessage))
    )

}

