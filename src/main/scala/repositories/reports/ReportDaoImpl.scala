package repositories.reports

import doobie.ConnectionIO
import doobie.util.transactor.Transactor
import models.dao.report.Report
import doobie.implicits._
import doobie.Read
import models.requests.report.AddReportRequest
import repositories.reports.ReportDaoImpl.Sql
import utils.DoobieMapping
import zio.{Task, URLayer, ZLayer}
import zio.interop.catz._

class ReportDaoImpl(master: Transactor[Task]) extends ReportDao {
  def addReport(report: AddReportRequest): Task[Unit] =
    Sql
      .insertReport(report)
      .transact(master)
      .unit

  def deleteReport(id: Long): Task[Unit] =
    Sql
      .deleteReport(id)
      .transact(master)
      .unit

  def getReportForUser(userId: Long, taskId: Long): Task[Option[Report]] =
    Sql
      .getUserReport(userId, taskId)
      .transact(master)

  def getReportsForTask(taskId: Long): Task[Seq[Report]] =
    Sql
      .getTasksReports(taskId)
      .transact(master)
}

object ReportDaoImpl {

  val live: URLayer[Transactor[Task], ReportDao] =
    ZLayer.fromFunction(new ReportDaoImpl(_))

  object Sql {

    import Mapping._
    def insertReport(report: AddReportRequest): ConnectionIO[Int] =
      sql"""
           INSERT INTO reports (task_id_for, author_id, comment, photo_data)
           VALUES (
              ${report.taskId},
              ${report.userId},
              ${report.comment},
              ${report.photo}
           )
         """
        .update
        .run

    def deleteReport(id: Long): ConnectionIO[Int] =
      sql""" DELETE FROM reports WHERE id = $id"""
        .update
        .run

    def getUserReport(userId: Long, taskId: Long): ConnectionIO[Option[Report]] =
      sql"""
            SELECT id, task_id_for, author_id, comment, photo_data
            FROM reports
            WHERE author_id = $userId AND task_id_for = $taskId
            ORDER BY id DESC LIMIT 1
         """
        .query[Report]
        .option

    def getTasksReports(taskId: Long): ConnectionIO[Seq[Report]] =
      sql"""
           SELECT (id, author_id, task_id_for, comment, photo_data)
           FROM reports
           WHERE task_id_for = $taskId
         """
        .query[Report]
        .to[Seq]

  }

  object Mapping extends DoobieMapping {

    implicit val readReport: Read[Report] =
      Read[(Long, Long, Long, Option[String], Option[List[Byte]])].map {
        case (id, taskId, userId, comment, photoData) =>
          Report(id, taskId, userId, comment, photoData)
      }

  }

}