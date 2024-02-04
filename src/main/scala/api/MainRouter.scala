package api

import api.user.UserRouter
import models.responses.GetUserResponse
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{Task, URLayer, ZLayer}

class MainRouter(userRouter: UserRouter) {

  // user
  def getUser: ZServerEndpoint[Any, Any] =
    userRouter.getUser

  // tasks
//  override def getTask: ServerEndpoint.Full[Unit, Unit, Int, String, GetUserResponse, Any, zio.Task] = {
//
//  }
}

object MainRouter {

  val live: URLayer[UserRouter, MainRouter] =
    ZLayer.fromFunction(new MainRouter(_))

}
