package models.responses

import io.circe.generic.JsonCodec
import models.dao.rating.Like
import org.joda.time.DateTime
import sttp.tapir.Schema

@JsonCodec
case class LikesResponse(likes: List[LikeResponse])

@JsonCodec
case class LikeResponse(
    id: Long,
    userIdLikeFor: Long,
    taskId: Long,
    authorLogin: String,
    taskName: String,
    createdAt: DateTime)

object LikeResponse {

  import utils.DateTimeJsonCodec._

  implicit lazy val sLikeResponse: Schema[LikeResponse] = Schema.derived
}

object LikesResponse {

  import utils.DateTimeJsonCodec._

  implicit lazy val sLikesResponse: Schema[LikesResponse] = Schema.derived

  def covertFromDao(like: Like, author: String, taskName: String): LikeResponse =
    LikeResponse(like.id, like.userIdLikeFor, like.taskId, author, taskName, like.createdAt)

}
