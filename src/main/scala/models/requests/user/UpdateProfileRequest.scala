package models.requests.user

import io.circe.generic.JsonCodec

@JsonCodec
case class UpdateProfileRequest(
    id: Int,
    profileDescription: Option[String],
    login: Option[String],
    photoUrl: Option[String])
