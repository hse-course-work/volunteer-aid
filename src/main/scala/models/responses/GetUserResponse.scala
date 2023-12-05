package models.responses

import io.circe.generic.JsonCodec

@JsonCodec
case class GetUserResponse (name: String, email: String)
