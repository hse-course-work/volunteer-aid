package models.dao.task

import models.dao.task.UserTask.Status
import org.joda.time.DateTime

case class UserTask(
    id: Long,
    creatorId: Long,
    description: String,
    status: Status,
    createdAt: DateTime,
    involvedCount: Int)

object UserTask {

  sealed trait Status {
    def id: Int
  }

  object Status {

    case object Active extends Status {
      override def id: Int = 0
    }

    case object Closed extends Status {
      override def id: Int = 1
    }

    def apply(id: Int): Status =
      id match {
        case 0 => Active
        case 1 => Closed
        case _ => throw new IllegalArgumentException(s"No status with id = $id")
      }
  }

}
