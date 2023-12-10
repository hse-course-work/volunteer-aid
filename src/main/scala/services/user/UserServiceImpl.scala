package services.user
import models.UserId
import models.user.User
import repositories.user.UserDao
import zio.Task

class UserServiceImpl(dao: UserDao) extends UserService {

  override def getUser(id: UserId): Task[User] =
    dao.get(id)

}
