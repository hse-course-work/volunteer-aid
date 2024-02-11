package models.requests.user

import io.circe.generic.JsonCodec

@JsonCodec
case class AuthenticateUserRequest(
    email: String,
    password: String)
