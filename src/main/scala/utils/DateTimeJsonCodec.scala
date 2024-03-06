package utils

import io.circe.{Decoder, Encoder}
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import sttp.tapir.Schema
import sttp.tapir.SchemaType.SString

import scala.util.{Failure, Success, Try}

object DateTimeJsonCodec {

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
