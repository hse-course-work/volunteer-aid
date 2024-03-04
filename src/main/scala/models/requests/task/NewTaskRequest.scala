package models.requests.task

import io.circe.generic.JsonCodec

@JsonCodec
case class NewTaskRequest(creatorId: Long)

// todo - тут должно быть много инфы про таску прикрепленные фото геолокацию
