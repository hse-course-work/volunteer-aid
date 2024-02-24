package api

import api.user.UserRouter
import models.responses.UserResponse
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{Task, URLayer, ZLayer}

class MainRouter(userRouter: UserRouter) {

  // user
  def getUser: ZServerEndpoint[Any, Any] =
    userRouter.getUser

  def authenticateUser: ZServerEndpoint[Any, Any] =
    userRouter.authenticateUser

  def sigInUser: ZServerEndpoint[Any, Any] =
    userRouter.signInUser

  def updateProfile: ZServerEndpoint[Any, Any] =
    userRouter.updateUserProfile

  // tasks
//  override def getTask: ServerEndpoint.Full[Unit, Unit, Int, String, GetUserResponse, Any, zio.Task] = {
//
//  }
}

object MainRouter {

  val live: URLayer[UserRouter, MainRouter] =
    ZLayer.fromFunction(new MainRouter(_))

}
