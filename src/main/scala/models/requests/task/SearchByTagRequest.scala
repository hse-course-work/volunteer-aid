package models.requests.task

import io.circe.generic.JsonCodec
@JsonCodec
case class SearchByTagRequest(tags: Seq[String], curX: Double, curY: Double, radius: Option[Int])
