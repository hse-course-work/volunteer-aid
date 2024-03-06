package repositories.task

import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import repositories.task.TaskDao.Filter
import zio.Task

trait TaskDao {

  def createTask(task: UserTask): Task[Unit]
  def updateTaskStatus(taskId: Long, newStatus: Status): Task[Unit]
  def get(id: Long): Task[Option[UserTask]]
  def getBy(filter: Filter): Task[Seq[UserTask]]
  def softDelete(id: Long): Task[Unit]

}

object TaskDao {

  sealed trait Filter

  object Filter {
    case class ByStatus(status: Status) extends Filter
    case class ByCreator(creatorId: Long) extends Filter
  }

}
