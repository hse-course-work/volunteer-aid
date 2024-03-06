package services.task

import models.UserId
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import models.dao.user.User
import models.requests.task.NewTaskRequest
import repositories.task.TaskDao
import repositories.task.TaskDao.Filter
import services.task.TaskService.TaskException
import services.task.TaskService.TaskException._
import services.user.UserService
import services.user.UserService.UserException
import services.user.UserService.UserException.UserNotFound
import zio.{&, IO, URLayer, ZIO, ZLayer}

class TaskServiceImpl(taskDao: TaskDao, userService: UserService) extends TaskService {
  def createTask(task: NewTaskRequest): IO[TaskException, UserTask] =
    for {
      _ <- checkCreatorExist(task.creatorId)
      taskModel <- ZIO.attempt(NewTaskRequest.toDao(task))
        .catchAll(e => ZIO.fail(BadStatus(e.getMessage)))
      _ <- taskDao.createTask(taskModel)
        .catchAll(e => ZIO.fail(InternalError(e)))
      tasks <- taskDao.getBy(Filter.ByCreator(task.creatorId))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield tasks.maxBy(_.createdAt)

  private def checkCreatorExist(userId: Long): IO[TaskException, User] =
    userService.getUser(UserId(userId))
      .mapError {
        case _: UserNotFound => CreatorProfileNotExist(userId)
        case e: UserException.InternalError => InternalError(e.e)
      }

  def updateTaskStatus(taskId: Long, newStatus: String): IO[TaskException, UserTask] =
    for {
      newStatus <- ZIO.attempt(Status.withName(newStatus))
        .catchAll(e => ZIO.fail(BadStatus(e.getMessage)))
      taskOpt <- taskDao.get(taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- taskOpt match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(TaskNotFound(taskId))
      }
      _ <- taskDao.updateTaskStatus(taskId, newStatus)
        .catchAll(e => ZIO.fail(InternalError(e)))
      task <- taskDao.get(taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield task.get

  def getTask(id: Long): IO[TaskException, UserTask] =
    taskDao.get(id)
      .catchAll(e => ZIO.fail(InternalError(e)))
      .flatMap {
        case Some(task) => ZIO.succeed(task)
        case None => ZIO.fail(TaskNotFound(id))
      }

  def getTasksWithStatus(statusName: String): IO[TaskException, Seq[UserTask]] =
    for {
      status <- ZIO.attempt(Status.withName(statusName))
        .catchAll(e => ZIO.fail(BadStatus(e.getMessage)))
      result <- taskDao.getBy(Filter.ByStatus(status))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield result


  def getTasksCreateBy(creatorId: Long): IO[TaskException, Seq[UserTask]] =
    for {
      _ <- checkCreatorExist(creatorId)
      result <- taskDao.getBy(Filter.ByCreator(creatorId))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield result

  def deleteTask(id: Long): IO[TaskException, Unit] =
    for {
      taskOpt <- taskDao.get(id)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- taskOpt match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(TaskNotFound(id))
      }
      _ <- taskDao.softDelete(id)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()
}

object TaskServiceImpl {

  val live: URLayer[TaskDao & UserService, TaskService] =
    ZLayer.fromFunction(new TaskServiceImpl(_, _))

}