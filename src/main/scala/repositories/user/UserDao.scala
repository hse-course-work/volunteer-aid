package repositories.user

import models.{Email, UserId}
import models.dao.user.User
import models.requests.user.UpdateProfileRequest
import zio.Task

trait UserDao {

  def getById(id: UserId): Task[Option[User]]
  def getByEmail(email: Email): Task[Option[User]]
  def insert(user: User): Task[Unit]

  def updateProfile(request: UpdateProfileRequest): Task[Unit]

  def deleteProfile(id: UserId): Task[Unit]

}
