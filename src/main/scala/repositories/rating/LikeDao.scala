package repositories.rating

import models.dao.rating.Like
import repositories.rating.LikeDao.Filter
import zio.Task

trait LikeDao {

  def createLike(like: Like): Task[Unit]

  def deleteLike(id: Long): Task[Unit]

  def getLikesBy(filter: Filter): Task[Seq[Like]]

  def get(userId: Long, taskId: Long): Task[Option[Like]]

}

object LikeDao {

  sealed trait Filter

  object Filter {
    case class ByUser(userId: Long) extends Filter

    case class ByTask(taskId: Long) extends Filter

    def withName(name: String, id: Long): Filter =
      name match {
        case "by-user" => ByUser(id)
        case "by-task" => ByTask(id)
        case _ => throw new IllegalArgumentException(s"No such filter: $name")
      }

  }


}

