package services.rating

import models.dao.rating.Like
import models.requests.rating.PutLikeRequest
import repositories.rating.LikeDao.Filter
import services.rating.RatingService.RatingException
import zio.IO

trait RatingService {

  def createLike(like: PutLikeRequest): IO[RatingException, Unit]

  def deleteLike(userId: Long, taskId: Long): IO[RatingException, Unit]

  def getLikesBy(filter: Filter): IO[RatingException, Seq[Like]]

}

object RatingService {

  sealed trait RatingException {
    def message: String
  }

  object RatingException {

    case class InternalError(e: Throwable) extends RatingException {
      override def message: String = s"Something went wrong, ${e.getMessage}"
    }

  }

}
