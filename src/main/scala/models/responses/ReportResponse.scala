package models.responses

import io.circe.generic.JsonCodec
import models.dao.report.Report

@JsonCodec
case class ReportResponse(
    id: Long,
    taskIdFor: Long,
    reportAuthorId: Long,
    comment: Option[String],
    photoData: Option[String])

object ReportResponse {
  def toResponse(report: Report): ReportResponse =
    ReportResponse(
      report.id,
      report.taskIdFor,
      report.reportAuthorId,
      report.comment,
      report.photoData
    )

}


@JsonCodec
case class ReportsResponse(reports: Seq[ReportResponse])
