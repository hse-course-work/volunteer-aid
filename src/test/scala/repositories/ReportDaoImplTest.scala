package repositories

import doobie.util.transactor.Transactor
import utils.{InitSchema, PostgresTestContainer}
import zio.{Scope, Task, ZIO, ZLayer}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}
import doobie.implicits._
import models.dao.report.Report
import models.requests.report.AddReportRequest
import repositories.reports.{ReportDao, ReportDaoImpl}
import zio.interop.catz._
import zio.test.Assertion.{equalTo, isUnit}
import zio.test.TestAspect.{after, before, sequential}

object ReportDaoImplTest extends ZIOSpecDefault {
  def spec: Spec[TestEnvironment with Scope, Any] =
    (suite("ReportDaoImpl")(
      creatingTable,
      addReport,
      getReportByUser
    ) @@ before(initTable) @@ after(cleanTable) @@ sequential)
      .provideLayer(makeLayer)

  private def cleanTable =
    for {
      xa <- ZIO.service[Transactor[Task]]
      _ <- sql" DELETE FROM reports ".update.run.transact(xa)
    } yield ()

  def initTable =
    ZIO.serviceWithZIO[Transactor[Task]] { xa =>
      for {
        _ <- InitSchema("/report.sql", xa)
        _ = println("схема готова!")
      } yield ()
    }

  def makeLayer = ZLayer.make[ReportDao with Transactor[Task]](
    PostgresTestContainer.defaultSettings,
    PostgresTestContainer.postgresTestContainer,
    PostgresTestContainer.xa,
    ReportDaoImpl.live
  )

  def creatingTable = {
    test("check table exists") {
      for {
        xa <- ZIO.service[Transactor[Task]]
        result <- sql"""
                       SELECT table_name
                       FROM information_schema.tables
                       WHERE table_schema = 'public' AND table_name LIKE 'reports';
                  """
          .query[String]
          .unique
          .transact(xa)
      } yield assertTrue(result == "reports")
    }
  }

  def addReport = {
    test("successful add report to user for task") {
      assertZIO(
        ZIO.serviceWithZIO[ReportDao](
          _.addReport(
            AddReportRequest(1, 1, Some("test"), None)
          )
        )
      )(isUnit)
    }
  }

  def getReportByUser = {
    test("successful get user reports by task") {
      assertZIO(
        ZIO.serviceWithZIO[ReportDao](
          _.getReportForUser(
            userId = 1,
            taskId = 1
          )
        )
      )(
        equalTo(
          Some(Report(1, 1, 1, Some("test"), None))
        )
      )
    }
  }

}
