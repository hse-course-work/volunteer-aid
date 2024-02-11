package models.user

import models._

case class User(
    id: UserId,
    email: Email,
    hashPassword: Password,
    profileDescription: Description,
    login: String,
    photoUrl: Option[String])

object User {

  def apply(
      email: Email,
      hashPassword: Password,
      profileDescription: Description,
      login: String,
      photoUrl: Option[String]): User =
    new User(
      id = DefaultId,
      email = email,
      hashPassword = hashPassword,
      profileDescription = profileDescription,
      login = login,
      photoUrl = photoUrl
    )

  private val DefaultId = UserId(0)
}
