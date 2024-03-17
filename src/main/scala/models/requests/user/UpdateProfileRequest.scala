package models.requests.user

import io.circe.generic.JsonCodec

@JsonCodec
case class UpdateProfileRequest(
    id: Long,
    profileDescription: Option[String],
    login: Option[String],
    photoData: Option[List[Byte]])
