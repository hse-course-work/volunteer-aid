package api

import api.task.TaskRouter
import api.user.UserRouter
import models.responses.UserResponse
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{&, Task, URLayer, ZLayer}

class MainRouter(userRouter: UserRouter, taskRouter: TaskRouter) {

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

  def getTask: ZServerEndpoint[Any, Any] =
    taskRouter.getTask

  def updateTaskWithStatus: ZServerEndpoint[Any, Any] =
    taskRouter.updateTaskWithStatus

  def createTaskByCreator: ZServerEndpoint[Any, Any] =
    taskRouter.createTaskByCreator

  def getSomeTasksByStatus: ZServerEndpoint[Any, Any] =
    taskRouter.getSomeTasksByStatus

  def getSomeTasksByCreator: ZServerEndpoint[Any, Any] =
    taskRouter.getSomeTasksByCreator

}

object MainRouter {

  val live: URLayer[UserRouter & TaskRouter, MainRouter] =
    ZLayer.fromFunction(new MainRouter(_, _))

}
