package models.responses

import io.circe.generic.JsonCodec
import models.{Description, Email, UserId}
import models.dao.user.User

@JsonCodec
case class UserResponse(id: Long, email: String, profileDescription: Option[String], login: String, photoData: Option[List[Byte]])

object UserResponse {

  def convert(user: User): UserResponse =
    UserResponse(
      UserId.unwrap(user.id),
      Email.unwrap(user.email),
      user.profileDescription.map(Description.unwrap),
      user.login,
      user.photoData
    )

}
