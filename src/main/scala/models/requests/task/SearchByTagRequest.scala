package models.requests.task

import io.circe.generic.JsonCodec
@JsonCodec
case class SearchByTagRequest(tags: Seq[String])
