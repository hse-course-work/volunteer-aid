package models.requests.user

import io.circe.generic.JsonCodec

@JsonCodec
case class SignInUserRequest(
    email: String,
    password: String,
    profileDescription: String,
    login: String,
    photoUrl: Option[String])
