package api.user

import models.responses.GetUserResponse
import sttp.tapir.{Endpoint, Tapir}
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce

trait UserApi extends Tapir with TapirJsonCirce with SchemaDerivation {
  private val defaultRoute = "api" / "user" / "v1"

  protected val get =
    endpoint
      .get
      .in(defaultRoute / path[Int](name= "id"))
      .out(jsonBody[GetUserResponse])
      .errorOut(stringBody)
}



