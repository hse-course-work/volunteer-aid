package models.requests.task

import io.circe.generic.JsonCodec
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import org.joda.time.DateTime
import sttp.tapir.Schema

@JsonCodec
case class NewTaskRequest(
    creatorId: Long,
    description: String,
    status: String,
    createdAt: DateTime,
    involvedCount: Int,
    x: Double,
    y: Double)

object NewTaskRequest {
  import utils.DateTimeJsonCodec._

  def toDao(task: NewTaskRequest): UserTask =
    UserTask(
      0L,
      task.creatorId,
      task.description,
      Status.withName(task.status),
      task.createdAt,
      task.involvedCount,
      task.x,
      task.y
    )

  implicit lazy val sTaskRequest: Schema[NewTaskRequest] = Schema.derived[NewTaskRequest]


}
