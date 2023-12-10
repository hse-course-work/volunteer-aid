package api.user

import models.responses.GetUserResponse
import models.user.User
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir._
import sttp.tapir.generic.auto.{SchemaDerivation, schemaForCaseClass}
import sttp.tapir.json.circe.{TapirJsonCirce, jsonBody}

trait UserApi {
  private val defaultRoute = "api" / "user" / "v1"

  protected val get =
    endpoint
      .get
      .in(defaultRoute / path[Int](name= "id"))
      .out(jsonBody[GetUserResponse])
      .errorOut(stringBody)
}



