package repositories.user

import models.{Email, UserId}
import models.dao.user.User
import zio.Task

trait UserDao {

  def getById(id: UserId): Task[Option[User]]

  def getByEmail(email: Email): Task[Option[User]]
  def insert(user: User): Task[Unit]

}
