package repositories.reports

import models.dao.report.Report
import models.requests.report.AddReportRequest
import zio.Task

trait ReportDao {

  def addReport(report: AddReportRequest): Task[Unit]

  def deleteReport(id: Long): Task[Unit]

  def getReportForUser(userId: Long, taskId: Long): Task[Option[Report]]

  def getReportsForTask(taskId: Long): Task[Seq[Report]]

}
