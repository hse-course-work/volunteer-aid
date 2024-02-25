package services.task

import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import repositories.task.TaskDao.Filter
import services.task.TaskService.TaskException
import zio.{IO, Task}

trait TaskService {

  def createTask(task: UserTask): IO[TaskException, Unit]

  def updateTaskStatus(taskId: Long, newStatus: Status): IO[TaskException, Unit]

  def getTask(id: Long): IO[TaskException, UserTask]

  def getTasksWithStatus(statusId: Int): IO[TaskException, Seq[UserTask]]

  def  getTasksCreateBy(creatorId: Int): IO[TaskException, Seq[UserTask]]

}

object TaskService {

  sealed trait TaskException {
    def message: String
  }

  object TaskException {

    case class TaskNotFound(id: Long) extends TaskException {
      override def message: String = s"Task with id = $id not found"
    }
    case class CreatorProfileNotExist(id: Long) extends TaskException {
      override def message: String = s"Creator with id = $id does not exist"
    }

    case class InternalError(e: Throwable) extends TaskException {
      override def message: String = s"Something went wrong, ${e.getMessage}"
    }

    case class BadStatus(message: String) extends TaskException
  }

}