package models.requests.task

import io.circe.generic.JsonCodec

@JsonCodec
case class UpdateTaskStatus(id: Long, newStatus: String)
