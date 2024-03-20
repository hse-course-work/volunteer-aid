package api.push

import models.responses.{PushResponse, PushesResponse}
import services.push.PushService
import sttp.model.StatusCode
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import zio.{URLayer, ZIO, ZLayer}

class PushRouter(pushService: PushService) extends PushApi {

  def getPushesForUser: ZServerEndpoint[Any, Any] =
    get.zServerLogic { userId =>
      pushService.getByUser(userId)
        .mapBoth(e => e.getMessage,
          result => (StatusCode.Ok, PushesResponse(result))
        )
    }

}

object PushRouter {

  val live: URLayer[PushService, PushRouter] =
    ZLayer.fromFunction(new PushRouter(_))

}
