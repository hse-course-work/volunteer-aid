package services.reports

import models.dao.report.Report
import models.requests.report.AddReportRequest
import repositories.reports.ReportDao
import zio.{Task, URLayer, ZIO, ZLayer}

class ReportServiceImpl(reportDao: ReportDao) extends ReportService {
  def addReport(report: AddReportRequest): Task[Unit] =
    reportDao.addReport(report)

  def deleteReport(id: Long): Task[Unit] =
    reportDao.deleteReport(id)

  def getReportForUser(userId: Long, taskId: Long): Task[Report] =
    reportDao.getReportForUser(userId, taskId)
      .flatMap(rep =>
        ZIO.fromOption(rep)
          .orElseFail(
            new IllegalAccessError(s"No report for user $userId and task $taskId")
          )
      )

  def getReportsForTask(taskId: Long): Task[Seq[Report]] =
    reportDao.getReportsForTask(taskId)
}

object ReportServiceImpl {
  val live: URLayer[ReportDao, ReportService] =
    ZLayer.fromFunction(new ReportServiceImpl(_))
}
