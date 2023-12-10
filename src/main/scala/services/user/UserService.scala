package services.user

import models.UserId
import models.user.User
import zio.{IO, Task}

trait UserService {

  def getUser(id: UserId): Task[User]

}
