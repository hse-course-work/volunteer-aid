package models.requests.task

import io.circe.{Decoder, Encoder}
import io.circe.generic.JsonCodec
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import sttp.tapir.codec.newtype._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import sttp.tapir.Schema
import NewTaskRequest._
import sttp.tapir.SchemaType.SString

import scala.util.{Failure, Success, Try}

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

  implicit val encodeDateTime: Encoder[DateTime] = Encoder.instance { dateTime =>
    Encoder.encodeString.apply(dateTime.toString(ISODateTimeFormat.dateTime()))
  }

  implicit val decodeDateTime: Decoder[DateTime] = Decoder.decodeString.emap { str =>
    Try(DateTime.parse(str, ISODateTimeFormat.dateTimeParser())) match {
      case Success(dateTime) => Right(dateTime)
      case Failure(exception) => Left(s"Не удалось декодировать DateTime: ${exception.getMessage}")
    }
  }

  implicit val dateTimeSchema: Schema[DateTime] = Schema(
    schemaType = SString[DateTime]()
  )


}
