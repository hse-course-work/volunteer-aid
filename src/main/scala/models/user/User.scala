package models.user

import models._


case class User(
    id: UserId,
    email: Email,
    hashPassword: Password,
    profileDescription: Description,
    login: String,
    photoUrl: Option[String])
