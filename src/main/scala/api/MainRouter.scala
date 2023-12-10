package api

import api.user.UserRouter
import models.responses.GetUserResponse
import sttp.tapir.ztapir.ZServerEndpoint
import zio.Task

class MainRouter(userRouter: UserRouter) {

  // user
  def getUser: ZServerEndpoint[Any, Any] =
    userRouter.getUser

  // tasks
//  override def getTask: ServerEndpoint.Full[Unit, Unit, Int, String, GetUserResponse, Any, zio.Task] = {
//
//  }
}
