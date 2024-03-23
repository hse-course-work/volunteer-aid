package repositories.push

import models.dao.push.Push
import doobie.{ConnectionIO, Read}
import doobie.util.transactor.Transactor
import doobie.implicits._
import org.joda.time.DateTime
import repositories.push.PushDaoImpl.Sql
import zio.interop.catz._
import utils.DoobieMapping
import zio.{Task, URLayer, ZLayer}

class PushDaoImpl(master: Transactor[Task]) extends PushDao {

  def getUserPushes(userId: Long): Task[Seq[Push]] =
    Sql
      .getByUser(userId)
      .transact(master)

  def addPushToUser(push: Push): Task[Unit] =
    Sql
      .insertPush(push)
      .transact(master)
      .unit
}

object PushDaoImpl {

  val live: URLayer[Transactor[Task], PushDao] =
    ZLayer.fromFunction(new PushDaoImpl(_))

  private val limit = 15

  object Sql extends DoobieMapping {

    def insertPush(push: Push): ConnectionIO[Int] =
      sql"""
            INSERT INTO pushes (user_id_to, task_id_for, message, created_at)
            VALUES (
              ${push.userIdTo},
              ${push.taskIdFor},
              ${push.message},
              ${push.createdAt}
            )
         """.update.run

    def getByUser(userId: Long): ConnectionIO[Seq[Push]] =
      sql"""
            SELECT id, user_id_to, task_id_for, message, created_at
            FROM pushes
            WHERE user_id_to = $userId
            ORDER BY created_at DESC
            LIMIT $limit
         """
        .query[Push]
        .to[Seq]

    implicit val readPush: Read[Push] =
      Read[(Long, Long, Long, String, DateTime)].map {
        case (id, userIdTo, taskIdFor, message, createdAt) =>
          Push(id, userIdTo, taskIdFor, message, createdAt)
      }

  }

}
