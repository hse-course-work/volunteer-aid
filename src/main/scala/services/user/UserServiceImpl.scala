package services.user
import models.UserId
import models.user.User
import repositories.user.UserDao
import zio.{Task, URLayer, ZLayer}

class UserServiceImpl(dao: UserDao) extends UserService {

  def getUser(id: UserId): Task[User] =
    dao.get(id)

}

object UserServiceImpl {

  val live: URLayer[UserDao, UserService] =
    ZLayer.fromFunction(new UserServiceImpl(_))

}
