package api.task

import models.UserId
import models.dao.task.UserTask.Status
import models.requests.task.HashtagRequest
import models.responses.{TaskResponse, UserResponse}
import services.hashtag.HashtagService
import services.task.TaskService
import services.task.TaskService.TaskException._
import sttp.model.StatusCode
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import zio.{&, URLayer, ZIO, ZLayer}
import sttp.tapir.generic.auto._

class TaskRouter(taskService: TaskService, hashtagService: HashtagService) extends TaskApi {

  def getTask: ZServerEndpoint[Any, Any] =
    get.zServerLogic(id =>
      taskService
        .getTask(id)
        .map(task => (StatusCode.Ok, TaskResponse.convert(task)))
        .catchAll {
          case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
          case e => ZIO.fail((StatusCode.InternalServerError, e.message))
        }
    )

  def updateTaskWithStatus: ZServerEndpoint[Any, Any] =
    updateTaskStatus.zServerLogic(statusUpdate =>
      taskService
        .updateTaskStatus(statusUpdate.id, statusUpdate.newStatus)
        .map(task => (StatusCode.Ok, TaskResponse.convert(task)))
        .catchAll {
          case e: BadStatus => ZIO.fail((StatusCode.BadRequest, e.message))
          case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
          case e => ZIO.fail((StatusCode.InternalServerError, e.message))
        }
    )

  def createTaskByCreator: ZServerEndpoint[Any, Any] =
    createNewTask.zServerLogic(newTask =>
      taskService
        .createTask(newTask)
        .map(task => (StatusCode.Ok, TaskResponse.convert(task)))
        .catchAll {
          case e: TaskAlreadyExist => ZIO.fail((StatusCode.BadRequest, e.message))
          case e: CreatorProfileNotExist => ZIO.fail((StatusCode.BadRequest, e.message))
          case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
          case e => ZIO.fail((StatusCode.InternalServerError, e.message))
        }
    )

  def getSomeTasksByStatus: ZServerEndpoint[Any, Any] =
    getTasksByStatus.zServerLogic(status =>
      taskService
        .getTasksWithStatus(status)
        .map(tasks => (StatusCode.Ok, tasks.map(TaskResponse.convert).toList))
        .catchAll {
          case e: BadStatus => ZIO.fail((StatusCode.BadRequest, e.message))
          case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
          case e => ZIO.fail((StatusCode.InternalServerError, e.message))
        }
    )

  def getSomeTasksByCreator: ZServerEndpoint[Any, Any] =
    getTasksByCreator.zServerLogic(id =>
      taskService
        .getTasksCreateBy(id)
        .map(tasks => (StatusCode.Ok, tasks.map(TaskResponse.convert).toList))
        .catchAll {
          case e: CreatorProfileNotExist => ZIO.fail((StatusCode.BadRequest, e.message))
          case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
          case e => ZIO.fail((StatusCode.InternalServerError, e.message))
        }
    )

  def delete: ZServerEndpoint[Any, Any] =
    deleteTask.zServerLogic(id =>
      taskService
        .deleteTask(id)
        .as(StatusCode.Ok)
        .catchAll {
          case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
          case e => ZIO.fail((StatusCode.InternalServerError, e.message))
        }
    )

  def addTag:  ZServerEndpoint[Any, Any] =
    addHashtag.zServerLogic(request =>
      for {
        model <- ZIO.attempt(HashtagRequest.toModel(request))
          .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
        result <- hashtagService.addHashtag(model)
          .as(StatusCode.Ok)
          .catchAll {
            case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
            case e => ZIO.fail((StatusCode.InternalServerError, e.message))
          }
      } yield result
    )

  def deleteTag: ZServerEndpoint[Any, Any] =
    deleteHashtag.zServerLogic(request =>
      for {
        model <- ZIO.attempt(HashtagRequest.toModel(request))
          .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
        result <- hashtagService.deleteHashtag(model)
          .as(StatusCode.Ok)
          .catchAll {
            case e: TaskNotFound => ZIO.fail((StatusCode.NotFound, e.message))
            case e => ZIO.fail((StatusCode.InternalServerError, e.message))
          }
      } yield result
    )


}

object TaskRouter {

  val live: URLayer[TaskService & HashtagService, TaskRouter] =
    ZLayer.fromFunction(new TaskRouter(_, _))

}
