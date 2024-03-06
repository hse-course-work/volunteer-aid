package services.rating

import models.dao.rating.Like
import models.requests.rating.PutLikeRequest
import org.joda.time.DateTime
import repositories.rating.LikeDao
import services.rating.RatingService.RatingException
import services.rating.RatingService.RatingException.InternalError
import zio.{IO, URLayer, ZIO, ZLayer}

class RatingServiceImpl(dao: LikeDao) extends RatingService {
  def createLike(like: PutLikeRequest): IO[RatingException, Unit] =
    for {
      likeOpt <- dao.get(like.userIdLikeFor, like.taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      likeModel = Like(0, like.userIdLikeFor, like.taskId, like.message, DateTime.now())
      _ <- ZIO.when(likeOpt.isEmpty)(dao.createLike(likeModel))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def deleteLike(userId: Long, taskId: Long): IO[RatingException, Unit] =
    for {
      likeOpt <- dao.get(userId, taskId)
        .catchAll(e => ZIO.fail(InternalError(e)))
      _ <- ZIO.when(likeOpt.nonEmpty)(dao.deleteLike(likeOpt.get.id))
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def getLikesBy(filter: LikeDao.Filter): IO[RatingException, Seq[Like]] =
    for {
      likes <- dao.getLikesBy(filter)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield likes
}

object RatingServiceImpl {

  val live: URLayer[LikeDao, RatingService] =
    ZLayer.fromFunction(new RatingServiceImpl(_))

}
