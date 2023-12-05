package api

import api.user.UserRouter
import models.responses.GetUserResponse

import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full
import zio.Task

class MainRouter(userRouter: UserRouter) {

  // user
  def getUser: Full[Unit, Unit, Int, String, GetUserResponse, Any, Task]  =
    userRouter.getUser

  // tasks
//  override def getTask: ServerEndpoint.Full[Unit, Unit, Int, String, GetUserResponse, Any, zio.Task] = {
//
//  }
}
