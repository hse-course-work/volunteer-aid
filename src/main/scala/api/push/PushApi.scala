package api.push

import models.responses.PushesResponse
import sttp.tapir.ztapir.{endpoint, path, statusCode, stringBody}
import sttp.tapir.ztapir._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.generic.auto._

trait PushApi {

  private val defaultRoute = "api" / "push" / "v1"

  protected val get =
    endpoint
      .get
      .in(defaultRoute / path[Long](name = "userId"))
      .out(statusCode)
      .out(jsonBody[PushesResponse])
      .errorOut(stringBody)

}
