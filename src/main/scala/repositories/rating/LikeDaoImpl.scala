package repositories.rating

import doobie.{ConnectionIO, Fragment, Read}
import doobie.util.transactor.Transactor
import models.dao.rating.Like
import doobie.implicits._
import org.joda.time.DateTime
import zio.interop.catz._
import repositories.rating.LikeDaoImpl.Sql
import repositories.rating.LikeDao.Filter
import repositories.rating.LikeDao.Filter._
import utils.DoobieMapping
import zio.{Task, URLayer, ZLayer}

class LikeDaoImpl(master: Transactor[Task]) extends LikeDao {
  def createLike(like: Like): Task[Unit] =
    Sql
      .putLike(like)
      .transact(master)
      .unit

  def deleteLike(likeId: Long): Task[Unit] =
    Sql
      .delete(likeId)
      .transact(master)
      .unit

  def getLikesBy(filter: LikeDao.Filter): Task[Seq[Like]] =
    Sql
      .get(filter)
      .transact(master)

  def get(userId: Long, taskId: Long): Task[Option[Like]] =
    Sql
      .get(userId, taskId)
      .transact(master)

}

object LikeDaoImpl {

  val live: URLayer[Transactor[Task], LikeDao] =
    ZLayer.fromFunction(new LikeDaoImpl(_))


  object Sql {
    import Mapping._

    def get(filter: Filter): ConnectionIO[Seq[Like]] =
      filter match {
        case ByUser(userId) => getUsersLike(userId)
        case ByTask(taskId) => getTaskLike(taskId)
      }

    private val baseSelect: Fragment =
      sql"""
            SELECT id, user_id_to, task_id, message, created_at
            FROM likes WHERE
         """

    private def getUsersLike(userId: Long): ConnectionIO[Seq[Like]] =
      (baseSelect ++ sql"user_id_to = $userId")
        .query[Like]
        .to[Seq]
    private def getTaskLike(taskId: Long): ConnectionIO[Seq[Like]] =
      (baseSelect ++ sql"task_id = $taskId")
        .query[Like]
        .to[Seq]

    def get(userId: Long, taskId: Long): ConnectionIO[Option[Like]] =
      (baseSelect ++ sql"user_id_to = $userId AND task_id = $taskId")
        .query[Like]
        .option

    def putLike(newLike: Like): ConnectionIO[Int] =
      sql"""
            INSERT INTO likes (
              user_id_to, task_id, message, created_at
            ) VALUES (
              ${newLike.userIdLikeFor},
              ${newLike.taskId},
              ${newLike.message},
              ${newLike.createdAt}
            )
         """
        .update
        .run

    def delete(id: Long): ConnectionIO[Int] =
      sql"DELETE FROM likes WHERE id = $id"
        .update
        .run

  }

  object Mapping extends DoobieMapping {

    implicit val readLike: Read[Like] =
      Read[(Long, Long, Long, String, DateTime)].map {
        case (id, userFor, taskId, message, createdAt) =>
          Like(id, userFor, taskId, message, createdAt)
      }

  }

}