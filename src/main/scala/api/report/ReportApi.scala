package api.report

import models.requests.report.AddReportRequest
import models.responses.{ReportResponse, ReportsResponse}
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir._
import sttp.tapir.generic.auto.schemaForCaseClass
trait ReportApi {

  private val defaultRoute = "api" / "report" / "v1"

  protected val deleteReport =
    endpoint
      .delete
      .in(defaultRoute / "delete" / path[Int](name = "id"))
      .out(statusCode)
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val addReport =
    endpoint
      .post
      .in(defaultRoute / "add")
      .in(jsonBody[AddReportRequest])
      .out(statusCode)
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val getUserReport =
    endpoint
      .get
      .in(defaultRoute / "user-report" / path[Int](name = "author-id") / path[Int](name = "task-id"))
      .out(statusCode)
      .out(jsonBody[ReportResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val getTaskReports =
    endpoint
      .get
      .in(defaultRoute / "task-reports" / path[Int](name = "task-id"))
      .out(statusCode)
      .out(jsonBody[ReportsResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

}
