package api.user

import models.responses.GetUserResponse

import sttp.tapir.server.ServerEndpoint.Full
import zio.{Task, ULayer, URLayer, ZIO, ZLayer}

class UserRouter extends UserApi {

  def getUser: Full[Unit, Unit, Int, String, GetUserResponse, Any, Task] = get.serverLogic(UserRouter.getUser)

}

object UserRouter {

  def getUser(id: Int): Task[Either[String, GetUserResponse]] =
    ZIO.succeed(Right(GetUserResponse(s"example_user_$id", "hello@world.com")))
}
