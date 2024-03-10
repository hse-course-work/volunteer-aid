package models.requests.task

import io.circe.generic.JsonCodec
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import org.joda.time.DateTime
import sttp.tapir.Schema

@JsonCodec
case class NewTaskRequest(
    creatorId: Long,
    name: String,
    description: String,
    involvedCount: Int,
    x: Double,
    y: Double)

object NewTaskRequest {
  import utils.DateTimeJsonCodec._

  def toDao(task: NewTaskRequest): UserTask =
    UserTask(
      0L,
      task.name,
      task.creatorId,
      task.description,
      Status.Active,
      DateTime.now(),
      task.involvedCount,
      task.x,
      task.y
    )

  implicit lazy val sTaskRequest: Schema[NewTaskRequest] = Schema.derived[NewTaskRequest]


}
