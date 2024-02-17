package models.responses

import io.circe.generic.JsonCodec
import models.{Description, Email, UserId}
import models.dao.user.User

@JsonCodec
case class UserResponse(id: Long, email: String, profileDescription: String, login: String, photoUrl: Option[String])

object UserResponse {

  def convert(user: User): UserResponse =
    UserResponse(
      UserId.unwrap(user.id),
      Email.unwrap(user.email),
      Description.unwrap(user.profileDescription),
      user.login,
      user.photoUrl
    )

}
