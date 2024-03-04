package api.user

import models.requests.user.{AuthenticateUserRequest, SignInUserRequest, UpdateProfileRequest}
import models.responses.UserResponse
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir._

trait UserApi {
  private val defaultRoute = "api" / "user" / "v1"

  protected val get =
    endpoint
      .get
      .in(defaultRoute / path[Int](name= "id"))
      .out(statusCode)
      .out(jsonBody[UserResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val authenticate =
    endpoint
      .post
      .in(defaultRoute / "authenticate")
      .in(jsonBody[AuthenticateUserRequest])
      .out(statusCode)
      .out(jsonBody[UserResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val signIn =
    endpoint
      .post
      .in(defaultRoute / "sing-in")
      .in(jsonBody[SignInUserRequest])
      .out(statusCode)
      .out(jsonBody[UserResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val updateUserInfo =
    endpoint
      .put
      .in(defaultRoute / "update-profile")
      .in(jsonBody[UpdateProfileRequest])
      .out(statusCode)
      .errorOut(statusCode)
      .errorOut(stringBody)

}



