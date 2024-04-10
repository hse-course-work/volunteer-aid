package models.dao.user

import models._

case class User(
    id: UserId,
    email: Email,
    hashPassword: Password,
    profileDescription: Option[Description],
    login: String,
    photoData: Option[String])

object User {

  def apply(
      email: Email,
      hashPassword: Password,
      profileDescription: Option[Description],
      login: String,
      photoData: Option[String]): User =
    new User(
      id = DefaultId,
      email = email,
      hashPassword = hashPassword,
      profileDescription = profileDescription,
      login = login,
      photoData = photoData
    )

  private val DefaultId = UserId(0)
}
