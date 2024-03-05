package api.task

import models.requests.task.{NewTaskRequest, UpdateTaskStatus}
import models.responses.TaskResponse
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir._
import TaskResponse._
import NewTaskRequest._

trait TaskApi {

  private val defaultRoute = "api" / "task" / "v1"

  protected val get =
    endpoint
      .get
      .in(defaultRoute / path[Int](name = "id"))
      .out(statusCode)
      .out(jsonBody[TaskResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)


  protected val updateTaskStatus =
    endpoint
      .put
      .in(defaultRoute / "set-status")
      .in(jsonBody[UpdateTaskStatus])
      .out(statusCode)
      .out(jsonBody[TaskResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val createNewTask =
    endpoint
      .post
      .in(defaultRoute / "new-task")
      .in(jsonBody[NewTaskRequest])
      .out(statusCode)
      .out(jsonBody[TaskResponse])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val getTasksByCreator =
    endpoint
      .get
      .in(defaultRoute / "by-creator" /path[Long](name = "id"))
      .out(statusCode)
      .out(jsonBody[List[TaskResponse]])
      .errorOut(statusCode)
      .errorOut(stringBody)

  protected val getTasksByStatus =
    endpoint
      .get
      .in(defaultRoute / "by-status" / path[String](name = "name"))
      .out(statusCode)
      .out(jsonBody[List[TaskResponse]])
      .errorOut(statusCode)
      .errorOut(stringBody)



}
