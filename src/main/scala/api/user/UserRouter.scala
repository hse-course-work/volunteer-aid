package api.user

import models.{Email, UserId}
import models.responses.UserResponse
import services.user.UserService
import sttp.model.StatusCode
import sttp.tapir.ztapir._
import zio.{Task, ULayer, URLayer, ZIO, ZLayer}

class UserRouter(userService: UserService) extends UserApi {

  def getUser: ZServerEndpoint[Any, Any] =
    get.zServerLogic(id =>
      userService
        .getUser(UserId(id))
        .map(user =>
          (StatusCode.Ok, UserResponse(id, Email.unwrap(user.email), s"user: ${user.id}", user.login, user.photoUrl))
        )
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
    )

  def authenticateUser: ZServerEndpoint[Any, Any] =
    authenticate.zServerLogic(request =>
      userService
        .authenticate(request)
        .map(user => (StatusCode.Ok, user))
        .catchAll(e =>
          ZIO.fail(
            (StatusCode.BadRequest, e.getMessage)
          )
        )
    )

  def signInUser: ZServerEndpoint[Any, Any] =
    signIn.zServerLogic(request =>
      userService
        .signIn(request)
        .map(user => (StatusCode.Ok, user))
        .catchAll(e =>
          ZIO.fail(
            (StatusCode.BadRequest, e.getMessage)
          )
        )
    )

}

object UserRouter {

  val live: URLayer[UserService, UserRouter] =
    ZLayer.fromFunction(new UserRouter(_))

}
