package services.task

import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import models.requests.task.NewTaskRequest
import repositories.task.TaskDao.Filter
import services.task.TaskService.TaskException
import zio.{IO, Task}

trait TaskService {

  def createTask(task: NewTaskRequest): IO[TaskException, UserTask]

  def updateTaskStatus(taskId: Long, newStatus: String): IO[TaskException, UserTask]

  def getTask(id: Long): IO[TaskException, UserTask]

  def getTasksWithStatus(statusId: String): IO[TaskException, Seq[UserTask]]

  def getTasksCreateBy(creatorId: Long): IO[TaskException, Seq[UserTask]]

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

    case class BadRequestForNewTask(fields: Seq[String]) extends TaskException {
      override def message: String = s"Fields: $fields in request have mistakes"
    }
  }

}