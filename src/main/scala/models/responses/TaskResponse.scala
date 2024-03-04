package models.responses

import io.circe.generic.JsonCodec
import org.joda.time.DateTime

@JsonCodec
case class TaskResponse(
    id: Long,
    creatorId: Long,
    description: String,
    status: String,
    createdAt: DateTime,
    involvedCount: Int,
    additionalInfo: AdditionalInfo)

@JsonCodec
case class AdditionalInfo()
