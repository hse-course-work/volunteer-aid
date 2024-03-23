package models.requests.report

import io.circe.generic.JsonCodec

@JsonCodec
case class AddReportRequest(
    taskId: Long,
    userId: Long,
    comment: Option[String],
    photo: Option[List[Byte]])

