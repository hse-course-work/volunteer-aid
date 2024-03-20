package models.responses

import io.circe.generic.JsonCodec
import models.dao.push.Push
import org.joda.time.DateTime
import sttp.tapir.Schema

@JsonCodec
case class PushesResponse(
    pushes: Seq[PushResponse])

object PushesResponse {

  import utils.DateTimeJsonCodec._

  implicit lazy val sPushesResponse: Schema[PushesResponse] = Schema.derived

}

@JsonCodec
case class PushResponse(
    id: Long,
    userIdTo: Long,
    taskIdFor: Long,
    taskForName: String,
    taskForAuthorLogin: String,
    message: String,
    createdAt: DateTime)

object PushResponse {

  import utils.DateTimeJsonCodec._

  implicit lazy val sPushResponse: Schema[PushResponse] = Schema.derived

}
