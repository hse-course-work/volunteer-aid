package models.responses

import io.circe.generic.JsonCodec
import models.dao.task.UserTask
import org.joda.time.DateTime
import sttp.tapir.Schema

@JsonCodec
case class TaskResponse(
    id: Long,
    name: String,
    creatorId: Long,
    description: String,
    status: String,
    createdAt: DateTime,
    involvedCount: Int,
    x: Double,
    y: Double)

object TaskResponse {

  import utils.DateTimeJsonCodec._
  implicit lazy val sTaskResponse: Schema[TaskResponse] = Schema.derived

  def convert(task: UserTask): TaskResponse =
    TaskResponse(
      task.id,
      task.name,
      task.creatorId,
      task.description,
      task.status.name,
      task.createdAt,
      task.involvedCount,
      task.xCoord,
      task.yCoord
    )
}

