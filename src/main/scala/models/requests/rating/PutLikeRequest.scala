package models.requests.rating

import io.circe.generic.JsonCodec

@JsonCodec
case class PutLikeRequest(userIdLikeFor: Long, taskId: Long, message: String)
