package repositories.task

import doobie.{ConnectionIO, Get, Put, Read}
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import org.joda.time.DateTime
import repositories.task.TaskDao.Filter
import repositories.task.TaskDao.Filter._
import repositories.task.TaskDaoImpl.Sql
import utils.DoobieMapping
import zio.{Task, URLayer, ZLayer}
import zio.interop.catz._


class TaskDaoImpl(master: Transactor[Task]) extends TaskDao {

  def createTask(task: UserTask): Task[Unit] =
    Sql
      .insertQuery(task)
      .transact(master)
      .unit

  def updateTaskStatus(taskId: Long, newStatus: Status): Task[Unit] =
    Sql
      .updateStatus(taskId, newStatus)
      .transact(master)
      .unit

  def get(id: Long): Task[Option[UserTask]] =
    Sql
      .getById(id)
      .transact(master)
  def getBy(filter: Filter): Task[Seq[UserTask]] = {
    val sqlQuery = filter match {
      case ByCreator(creatorId) => Sql.getByCreator(creatorId)
      case ByStatus(status) => Sql.getByStatus(status)
    }

    sqlQuery.transact(master)
  }

  def softDelete(id: Long): Task[Unit] =
    Sql
      .deleteTask(id)
      .transact(master)
      .unit


}

object TaskDaoImpl {

  val live: URLayer[Transactor[Task], TaskDao] =
    ZLayer.fromFunction(new TaskDaoImpl(_))

  object Sql {
    import Mapping._
    def insertQuery(task: UserTask): ConnectionIO[Int] =
      sql"""
            INSERT INTO tasks (
            creator_id, description, status_id, created_at, involved_count, x_coord, y_coord
            )
            VALUES (
              ${task.creatorId},
              ${task.description},
              ${task.status.id},
              ${task.createdAt},
              ${task.involvedCount},
              ${task.xCoord},
              ${task.yCoord}
            )
         """.update.run

    def updateStatus(id: Long, newStatus: Status): ConnectionIO[Int] =
      sql"""
            UPDATE tasks SET status = $newStatus
            WHERE id = $id
         """
        .update
        .run

    private val baseGetQuery =
      sql"""
            SELECT id, creator_id, description, status_id, created_at, involved_count
            FROM tasks WHERE
         """

    def getById(id: Long): ConnectionIO[Option[UserTask]] =
      (baseGetQuery ++ sql" id = $id ")
        .query[UserTask]
        .option

    def getByCreator(creatorId: Long): ConnectionIO[Seq[UserTask]] =
      (baseGetQuery ++ sql" creator_id = $creatorId ")
        .query[UserTask]
        .to[Seq]

    def getByStatus(status: Status): ConnectionIO[Seq[UserTask]] =
      (baseGetQuery ++ sql" status = ${status.id} ")
        .query[UserTask]
        .to[Seq]

    def deleteTask(id: Long): ConnectionIO[Int] =
      updateStatus(id, Status.Delete)


  }

  object Mapping extends DoobieMapping {

    implicit val putStatus: Put[Status] =
      Put[Int].tcontramap[Status](status => status.id)

    implicit val getStatus: Get[Status] =
      Get[Int].map(id => Status(id))

    implicit val readUserTask: Read[UserTask] =
      Read[(Long, Long, String, Int, DateTime, Int, Double, Double)].map {
        case (id, creatorId, description, statusId, createdAt, involvedCount, x, y) =>
          UserTask(id, creatorId, description, Status(statusId), createdAt, involvedCount, x, y)
      }

  }

}