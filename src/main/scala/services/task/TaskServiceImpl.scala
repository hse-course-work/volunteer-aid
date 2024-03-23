package services.task

import models.UserId
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import models.dao.user.User
import models.requests.task.NewTaskRequest
import repositories.task.TaskDao
import repositories.task.TaskDao.Filter
import services.push.PushService
import services.task.TaskService.TaskException
import services.task.TaskService.TaskException._
import services.user.UserService
import services.user.UserService.UserException
import services.user.UserService.UserException.UserNotFound
import zio.{&, IO, Task, UIO, URIO, URLayer, ZIO, ZLayer}

class TaskServiceImpl(taskDao: TaskDao, userService: UserService, pushService: PushService) extends TaskService {

  def createTask(task: NewTaskRequest): IO[TaskException, UserTask] =
    for {
      _ <- checkCreatorExist(task.creatorId)
      maybeTask <- checkTaskExist(task.name, task.creatorId)
      _ <- ZIO.when(maybeTask.nonEmpty)(ZIO.fail(TaskAlreadyExist(task.name, task.creatorId)))
      taskModel <- ZIO
        .attempt(NewTaskRequest.toDao(task))
        .catchAll(e => ZIO.fail(BadStatus(e.getMessage)))
      _ <- taskDao
        .createTask(taskModel)
        .catchAll(e => ZIO.fail(InternalError(e)))
      tasks <- taskDao
        .getBy(Filter.ByCreator(task.creatorId))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield tasks.maxBy(_.createdAt)

  private def checkCreatorExist(userId: Long): IO[TaskException, User] =
    userService
      .getUser(UserId(userId))
      .mapError {
        case _: UserNotFound => CreatorProfileNotExist(userId)
        case e: UserException.InternalError => InternalError(e.e)
      }

  private def checkTaskExist(taskName: String, userId: Long): IO[TaskException, Option[UserTask]] =
    taskDao
      .getBy(Filter.ByCreator(userId))
      .mapBoth(
        e => InternalError(e),
        _.find(_.name == taskName)
      )

  def updateTaskStatus(taskId: Long, newStatus: String): IO[TaskException, UserTask] =
    for {
      newStatus <- ZIO
        .attempt(Status.withName(newStatus))
        .catchAll(e => ZIO.fail(BadStatus(e.getMessage)))
      taskOpt <- taskDao
        .get(taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- taskOpt match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(TaskNotFound(taskId))
      }
      _ <- taskDao
        .updateTaskStatus(taskId, newStatus)
        .catchAll(e => ZIO.fail(InternalError(e)))
      task <- taskDao
        .get(taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- pushService
        .sendPushWhenChangedStatus(newStatus, task.get)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield task.get

  def getTask(id: Long): IO[TaskException, UserTask] =
    taskDao
      .get(id)
      .catchAll(e => ZIO.fail(InternalError(e)))
      .flatMap {
        case Some(task) => ZIO.succeed(task)
        case None => ZIO.fail(TaskNotFound(id))
      }

  def getTasksWithStatus(statusName: String): IO[TaskException, Seq[UserTask]] =
    for {
      status <- ZIO
        .attempt(Status.withName(statusName))
        .catchAll(e => ZIO.fail(BadStatus(e.getMessage)))
      result <- taskDao
        .getBy(Filter.ByStatus(status))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield result

  def getTasksCreateBy(creatorId: Long): IO[TaskException, Seq[UserTask]] =
    for {
      _ <- checkCreatorExist(creatorId)
      result <- taskDao
        .getBy(Filter.ByCreator(creatorId))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield result

  def deleteTask(id: Long): IO[TaskException, Unit] =
    for {
      taskOpt <- taskDao
        .get(id)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- taskOpt match {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(TaskNotFound(id))
      }
      _ <- taskDao
        .softDelete(id)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- pushService.sendPushWhenChangedStatus(Status.Delete, taskOpt.get)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def getTakenTasks(userId: Long): IO[TaskException, Seq[UserTask]] =
    taskDao
      .getTakenTasks(userId)
      .map(_.filter(_.status != Status.Delete))
      .catchAll(e => ZIO.fail(InternalError(e)))

  def takeTaskInWork(userId: Long, taskId: Long): Task[Unit] =
    for {
      taken <- taskDao.getTakenTasks(userId)
      _ <- ZIO.when(!taken.map(_.id).contains(taskId))(
        taskDao.takeTaskInWork(userId, taskId).as(
          pushService.sendPushWhenUserTakeYourTask(userId, taken.find(_.id == taskId).get)
        )
      )
    } yield ()

  def removeFromTaken(userId: Long, taskId: Long): Task[Unit] =
    for {
      taken <- taskDao.getTakenTasks(userId)
      _ <- ZIO.when(taken.map(_.id).contains(taskId))(
        taskDao.removeTakenTask(userId, taskId)
      )
    } yield ()
}

object TaskServiceImpl {

  val live: URLayer[TaskDao & UserService & PushService, TaskService] =
    ZLayer.fromFunction(new TaskServiceImpl(_, _, _))

}
