package api.report

import models.responses.{ReportResponse, ReportsResponse}
import services.reports.ReportService
import sttp.model.StatusCode
import sttp.tapir.ztapir.{RichZEndpoint, ZServerEndpoint}
import zio.{URLayer, ZIO, ZLayer}


class ReportRouter(reportService: ReportService) extends ReportApi {

  def addUserReport: ZServerEndpoint[Any, Any] =
    addReport.zServerLogic(request =>
      reportService.addReport(request)
        .as(StatusCode.Ok)
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
    )

  def deleteUserReport: ZServerEndpoint[Any, Any] =
    deleteReport.zServerLogic(reportId =>
      reportService.deleteReport(reportId)
        .as(StatusCode.Ok)
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
    )

  def getReportForUser: ZServerEndpoint[Any, Any] =
    getUserReport.zServerLogic {
      case (userId, taskId) =>
      reportService.getReportForUser(userId, taskId)
        .map(report => (StatusCode.Ok, ReportResponse.toResponse(report)))
        .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
    }

  def getTasksReportForUser: ZServerEndpoint[Any, Any] =
    getTaskReports.zServerLogic {
      case  (taskId) =>
        reportService.getReportsForTask(taskId)
          .map(reports => (StatusCode.Ok, ReportsResponse(reports.map(ReportResponse.toResponse))))
          .catchAll(e => ZIO.fail((StatusCode.BadRequest, e.getMessage)))
    }

}

object ReportRouter {

  val live: URLayer[ReportService, ReportRouter] =
    ZLayer.fromFunction(new ReportRouter(_))

}
