package models.requests.user

import io.circe.generic.JsonCodec
import models._
import models.dao.user.User

@JsonCodec
case class SignInUserRequest(
    email: String,
    password: String,
    profileDescription: Option[String],
    login: String,
    photoData: Option[List[Byte]])

object SignInUserRequest {

  def toDaoModel(request: SignInUserRequest): User =
    User(
      Email(request.email),
      Password(request.password),
      request.profileDescription.map(str => Description(str)),
      request.login,
      request.photoData
    )

}
