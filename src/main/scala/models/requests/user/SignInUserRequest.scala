package models.requests.user

import io.circe.generic.JsonCodec
import models._
import models.dao.user.User

@JsonCodec
case class SignInUserRequest(
    email: String,
    password: String,
    profileDescription: String,
    login: String,
    photoUrl: Option[String])

object SignInUserRequest {

  def toDaoModel(request: SignInUserRequest): User =
    User(
      Email(request.email),
      Password(request.password),
      Description(request.profileDescription),
      request.login,
      request.photoUrl
    )

}
