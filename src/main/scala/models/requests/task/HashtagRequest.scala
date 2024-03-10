package models.requests.task

import io.circe.generic.JsonCodec
import models.dao.hashtag.Hashtag
import models.dao.hashtag.Hashtag.Tag


@JsonCodec
case class HashtagRequest(value: String, taskId: Long)

object HashtagRequest {

  def toModel(request: HashtagRequest): Hashtag =
    Hashtag(Tag.withName(request.value), request.taskId)

}
