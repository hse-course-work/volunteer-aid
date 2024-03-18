package services.push

import models.UserId
import models.dao.push.Push
import models.dao.rating.Like
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import org.joda.time.DateTime
import repositories.push.PushDao
import repositories.task.TaskDao
import repositories.user.UserDao
import zio.{&, Task, URLayer, ZIO, ZLayer}

class PushServiceImpl(taskDao: TaskDao, pushDao: PushDao, userDao: UserDao) extends PushService {

  def getByUser(userId: Long): Task[Seq[Push]] =
    pushDao.getUserPushes(userId)

  def sendPushWhenChangedStatus(newStatus: UserTask.Status, task: UserTask): Task[Unit] =
    for {
      users <- taskDao.getUsersWhoTakeTask(task.id)
      _ <- ZIO.foreachDiscard(users) { userId =>
        userDao.getById(UserId(userId)).flatMap { userOpt =>
          val user = userOpt.get
          val message = newStatus match {
            case Status.Delete => s"Пользователь ${user.login} удалил задачу '${task.name}'"
            case Status.Closed =>
              s"Пользователь ${user.login} закрыл задачу '${task.name}. Возможна она уже решена или больше не требует решения"
            case Status.Active =>
              s"Пользователь ${user.login} открыл задачу '${task.name}. Автор считает, что задача снова актуальна"
          }
          pushDao.addPushToUser(
            Push(
              id = 0,
              userIdTo = userId,
              taskIdFor = task.id,
              message = message,
              createdAt = DateTime.now()
            )
          )
        }
      }
    } yield ()

  def sendPushWhenGetLike(like: Like): Task[Unit] =
    for {
      task <- taskDao.get(like.taskId)
      userWhoPut <- userDao.getById(UserId(task.get.creatorId))
      message = s"Пользователь ${userWhoPut.get.login} благодарит Вас за старания в рамках его задачи '${task.get.name}'"
      _ <- pushDao.addPushToUser(
        Push(
          id = 0, userIdTo = like.userIdLikeFor, taskIdFor = like.taskId, message = message, createdAt = DateTime.now()
        )
      )
    } yield ()

  def sendPushWhenUserTakeYourTask(userWhoTakeId: Long, task: UserTask): Task[Unit] =
    for {
      user <- userDao.getById(UserId(userWhoTakeId))
      message = s"Пользователь ${user.get.login} откликнулся на вашу задачу `${task.name}`"
      _ <- pushDao.addPushToUser(
        Push(
          id = 0, userIdTo = task.creatorId, taskIdFor = task.id, message = message, createdAt = DateTime.now()
        )
      )
    } yield ()
}

object PushServiceImpl {
  val live: URLayer[TaskDao & PushDao & UserDao, PushService] =
    ZLayer.fromFunction(new PushServiceImpl(_, _, _))
}