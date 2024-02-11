package repositories.user

import models.UserId
import models.user.User
import zio.Task

trait UserDao {

  def get(id: UserId): Task[User]
  def insert(user: User): Task[Unit]

}
