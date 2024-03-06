package api.rating

import models.requests.rating.PutLikeRequest
import models.responses.LikesResponse
import sttp.tapir.ztapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._

trait RatingApi {

  private val defaultRoute = "api" / "rating" / "v1"

  protected val get =
    endpoint
      .get
      .in(defaultRoute / path[String](name = "filter") / path[Int](name = "id"))
      .out(statusCode)
      .out(jsonBody[LikesResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val create =
    endpoint
      .post
      .in(defaultRoute / "put-like")
      .in(jsonBody[PutLikeRequest])
      .out(statusCode)
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val delete =
    endpoint
      .delete
      .in(defaultRoute / "delete-like" / path[Int](name = "user_id") / path[Int](name = "task_id"))
      .out(statusCode)
      .errorOut(statusCode)
      .errorOut(stringBody)

}
