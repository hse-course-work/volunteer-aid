package models.dao.task

import models.dao.task.UserTask.Status
import org.joda.time.DateTime

case class UserTask(
    id: Long,
    name: String,
    creatorId: Long,
    description: String,
    status: Status,
    createdAt: DateTime,
    involvedCount: Int,
    xCoord: Double,
    yCoord: Double)

object UserTask {

  sealed trait Status {
    def id: Int
    def name: String
  }

  object Status {

    def withName(name: String): Status =
      name match {
        case "active" => Active
        case "closed" => Closed
        case "delete" => Delete
        case _ => throw new IllegalArgumentException(s"No status with name = $name")
      }

    case object Active extends Status {
      override def id: Int = 0
      override def name: String = "active"
    }

    case object Closed extends Status {
      override def id: Int = 1
      override def name: String = "closed"
    }

    case object Delete extends Status {
      override def id: Int = 2

      override def name: String = "delete"
    }

    def apply(id: Int): Status =
      id match {
        case 0 => Active
        case 1 => Closed
        case 2 => Delete
        case _ => throw new IllegalArgumentException(s"No status with id = $id")
      }
  }

}
