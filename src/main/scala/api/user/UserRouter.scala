package api.user

import models.{Email, UserId}
import models.responses.UserResponse
import services.user.UserService
import services.user.UserService.UserException._
import sttp.model.StatusCode
import sttp.tapir.ztapir._
import zio.{Task, ULayer, URLayer, ZIO, ZLayer}

class UserRouter(userService: UserService) extends UserApi {

  def getUser: ZServerEndpoint[Any, Any] =
    get.zServerLogic(id =>
      userService
        .getUser(UserId(id))
        .map(user =>
          (StatusCode.Ok, UserResponse.convert(user))
        )
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
    )

  def authenticateUser: ZServerEndpoint[Any, Any] =
    authenticate.zServerLogic(request =>
      userService
        .authenticate(request)
        .map(user => (StatusCode.Ok, user))
        .catchAll {
          case e: UserNotFound => ZIO.fail((StatusCode.NotFound, e.msg))
          case e: InternalError => ZIO.fail((StatusCode.InternalServerError, e.msg))
          case _ => ZIO.fail((StatusCode.InternalServerError, "server error"))
        }
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
