package models.responses

import io.circe.{Decoder, Encoder}
import io.circe.generic.JsonCodec
import models.dao.task.UserTask
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import sttp.tapir.codec.newtype._
import TaskResponse._
import sttp.tapir.Schema
import sttp.tapir.SchemaType.SString

import scala.util.{Failure, Success, Try}

@JsonCodec
case class TaskResponse(
    id: Long,
    creatorId: Long,
    description: String,
    status: String,
    createdAt: DateTime,
    involvedCount: Int,
    x: Double,
    y: Double)

object TaskResponse {


  implicit lazy val sTaskResponse: Schema[TaskResponse] = Schema.derived

  def convert(task: UserTask): TaskResponse =
    TaskResponse(
      task.id,
      task.creatorId,
      task.description,
      task.status.name,
      task.createdAt,
      task.involvedCount,
      task.xCoord,
      task.yCoord
    )

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

