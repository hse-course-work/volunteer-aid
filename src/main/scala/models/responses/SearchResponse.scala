package models.responses

import io.circe.generic.JsonCodec
import models.dao.task.UserTask

@JsonCodec
case class SearchResponse(tasks: Seq[TasksByCategory])

@JsonCodec
case class TasksByCategory(category: String, tasks: Seq[TaskResponse])

object SearchResponse {

  def toResponse(map: Map[String, Seq[UserTask]]): SearchResponse = {
    val tasks = map.map {
      case (tag, tasks) =>
        TasksByCategory(tag, tasks.map(TaskResponse.convert))
    }
      .toSeq
    SearchResponse(tasks)
  }

}