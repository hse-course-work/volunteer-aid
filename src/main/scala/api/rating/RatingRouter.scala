package api.rating

import models.responses.{LikeResponse, LikesResponse}
import repositories.rating.LikeDao.Filter
import services.rating.RatingService
import sttp.model.StatusCode
import services.rating.RatingService.RatingException._
import zio.{URLayer, ZIO, ZLayer}
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}

class RatingRouter(ratingService: RatingService) extends RatingApi {

  def getBy: ZServerEndpoint[Any, Any] =
    get.zServerLogic { case (filterName, id) =>
      ZIO
        .attempt(Filter.withName(filterName, id))
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
        .flatMap(filter =>
          ratingService
            .getLikesBy(filter)
            .map(likes => (StatusCode.Ok, LikesResponse(likes.map(LikesResponse.covertFromDao).toList)))
            .catchAll { case e: InternalError =>
              ZIO.fail((StatusCode.InternalServerError, e.message))
            }
        )
    }

  def putLike: ZServerEndpoint[Any, Any] =
    create.zServerLogic(request =>
      ratingService
        .createLike(request)
        .as(StatusCode.Ok)
        .catchAll(e => ZIO.fail((StatusCode.InternalServerError, e.message)))
    )

  def deleteLike: ZServerEndpoint[Any, Any] =
    delete.zServerLogic { case (userId, taskId) =>
      ratingService
        .deleteLike(userId, taskId)
        .as(StatusCode.Ok)
        .catchAll(e => ZIO.fail((StatusCode.InternalServerError, e.message)))
    }

}

object RatingRouter {

  val live: URLayer[RatingService, RatingRouter] =
    ZLayer.fromFunction(new RatingRouter(_))

}
