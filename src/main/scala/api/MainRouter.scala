package api

import api.push.PushRouter
import api.rating.RatingRouter
import api.task.TaskRouter
import api.user.UserRouter
import models.responses.UserResponse
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{&, Task, URLayer, ZLayer}

class MainRouter(userRouter: UserRouter, taskRouter: TaskRouter, ratingRouter: RatingRouter, pushRouter: PushRouter) {

  // user
  def getUser: ZServerEndpoint[Any, Any] =
    userRouter.getUser

  def authenticateUser: ZServerEndpoint[Any, Any] =
    userRouter.authenticateUser

  def sigInUser: ZServerEndpoint[Any, Any] =
    userRouter.signInUser

  def updateProfile: ZServerEndpoint[Any, Any] =
    userRouter.updateUserProfile

  def deleteUser:  ZServerEndpoint[Any, Any] =
    userRouter.deleteProfile

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

  def deleteTask: ZServerEndpoint[Any, Any] =
    taskRouter.delete

  def takeTaskInWork: ZServerEndpoint[Any, Any] =
    taskRouter.takeTaskInWork

  def getTakenTask: ZServerEndpoint[Any, Any] =
    taskRouter.getTakenTasks

  def removeFromWork: ZServerEndpoint[Any, Any] =
    taskRouter.removeTaskFromWork

  // rating
  def getLikesBy: ZServerEndpoint[Any, Any] =
    ratingRouter.getLikesBy

  def putLike: ZServerEndpoint[Any, Any] =
    ratingRouter.putLike

  def deleteLike: ZServerEndpoint[Any, Any] =
    ratingRouter.deleteLike

  // tag

  def addHashtag: ZServerEndpoint[Any, Any] =
    taskRouter.addTag

  def deleteHashtag: ZServerEndpoint[Any, Any] =
    taskRouter.deleteTag

  def searchByTags: ZServerEndpoint[Any, Any] =
    taskRouter.searchByTag

  // push

  def getPushesForUser: ZServerEndpoint[Any, Any] =
    pushRouter.getPushesForUser


}

object MainRouter {

  val live: URLayer[UserRouter & TaskRouter & RatingRouter & PushRouter, MainRouter] =
    ZLayer.fromFunction(new MainRouter(_, _, _, _))

}
