package services.rating

import models.UserId
import models.dao.rating.Like
import models.requests.rating.PutLikeRequest
import org.joda.time.DateTime
import repositories.rating.LikeDao
import services.push.PushService
import services.rating.RatingService.RatingException
import services.rating.RatingService.RatingException.{InternalError, ServerError}
import services.task.TaskService
import services.user.UserService
import zio.{&, IO, URLayer, ZIO, ZLayer}

class RatingServiceImpl(dao: LikeDao, pushService: PushService, userService: UserService, taskService: TaskService) extends RatingService {

  def createLike(like: PutLikeRequest): IO[RatingException, Unit] =
    for {
      likeOpt <- dao
        .get(like.userIdLikeFor, like.taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      likeModel = Like(0, like.userIdLikeFor, like.taskId, DateTime.now())
      _ <- {
        ZIO
          .when(likeOpt.isEmpty)(dao.createLike(likeModel))
          .catchAll(e => ZIO.fail(InternalError(e)))
      } *> pushService
        .sendPushWhenGetLike(likeModel)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def deleteLike(userId: Long, taskId: Long): IO[RatingException, Unit] =
    for {
      likeOpt <- dao
        .get(userId, taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- ZIO
        .when(likeOpt.nonEmpty)(dao.deleteLike(likeOpt.get.id))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def getLikesBy(filter: LikeDao.Filter): IO[RatingException, Seq[(Like, String, String)]] =
    for {
      likes <- dao
        .getLikesBy(filter)
        .catchAll(e => ZIO.fail(InternalError(e)))
      likesWithInfo <- ZIO.foreach(likes) { like =>
        for {
          task <- taskService.getTask(like.taskId).catchAll(e => ZIO.fail(ServerError(e.message)))
          user <- userService.getUser(UserId(task.creatorId)).catchAll(e => ZIO.fail(ServerError(e.msg)))
        } yield (like, user.login, task.name)
      }
    } yield likesWithInfo
}

object RatingServiceImpl {

  val live: URLayer[LikeDao & PushService & UserService & TaskService, RatingService] =
    ZLayer.fromFunction(new RatingServiceImpl(_, _, _, _))

}
