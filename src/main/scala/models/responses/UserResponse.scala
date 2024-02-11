package models.responses

import io.circe.generic.JsonCodec
import models.Email

@JsonCodec
case class UserResponse(id: Long, email: String, profileDescription: String, login: String, photoUrl: Option[String])
