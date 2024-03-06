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
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.msg)))
    )

  def authenticateUser: ZServerEndpoint[Any, Any] =
    authenticate.zServerLogic(request =>
      userService
        .authenticate(request)
        .map(user => (StatusCode.Ok, user))
        .catchAll {
          case e: UserNotFound => ZIO.fail((StatusCode.NotFound, e.msg))
          case e: BadEmailOrPassword => ZIO.fail((StatusCode.BadRequest, e.msg))
          case e: InternalError => ZIO.fail((StatusCode.InternalServerError, e.msg))
          case _ => ZIO.fail((StatusCode.InternalServerError, "server error"))
        }
    )

  def signInUser: ZServerEndpoint[Any, Any] =
    signIn.zServerLogic(request =>
      userService
        .signIn(request)
        .map(user => (StatusCode.Ok, user))
        .catchAll {
          case e: ProfileWithEmailAlreadyExist => ZIO.fail((StatusCode.NotAcceptable, e.msg))
          case e: BadEmailOrPassword => ZIO.fail((StatusCode.BadRequest, e.msg))
          case e: InternalError => ZIO.fail((StatusCode.InternalServerError, e.msg))
          case _ => ZIO.fail((StatusCode.InternalServerError, "server error"))
        }
    )

  def updateUserProfile: ZServerEndpoint[Any, Any] =
    updateUserInfo.zServerLogic(request =>
      userService
        .updateUserInfo(request)
        .as(StatusCode.Ok)
        .catchAll {
          case e: UserNotFound => ZIO.fail((StatusCode.NotFound, e.msg))
          case e: InternalError => ZIO.fail((StatusCode.InternalServerError, e.msg))
          case _ => ZIO.fail((StatusCode.InternalServerError, "server error"))
        }
    )

  def deleteProfile: ZServerEndpoint[Any, Any] =
    deleteUser.zServerLogic(request =>
      userService
        .deleteUserProfile(UserId(request))
        .as(StatusCode.Ok)
        .catchAll {
          case e: UserNotFound => ZIO.fail((StatusCode.NotFound, e.msg))
          case e: InternalError => ZIO.fail((StatusCode.InternalServerError, e.msg))
          case _ => ZIO.fail((StatusCode.InternalServerError, "server error"))
        }
    )

}

object UserRouter {

  val live: URLayer[UserService, UserRouter] =
    ZLayer.fromFunction(new UserRouter(_))

}
