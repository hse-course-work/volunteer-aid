package services.reports

import models.dao.report.Report
import models.requests.report.AddReportRequest
import zio.Task

trait ReportService {

  def addReport(report: AddReportRequest): Task[Unit]

  def deleteReport(id: Long): Task[Unit]

  def getReportForUser(userId: Long, taskId: Long): Task[Report]

  def getReportsForTask(taskId: Long): Task[Seq[Report]]

}
