package repositories

import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import doobie.implicits._
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import org.joda.time.DateTime
import repositories.task.TaskDao.Filter
import repositories.task.{TaskDao, TaskDaoImpl}
import utils.{DoobieMapping, InitSchema, PostgresTestContainer}
import zio.interop.catz._
import zio.test.Assertion.isUnit
import zio.test.TestAspect.{after, before, sequential}
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}
import zio.{Scope, Task, ZIO, ZLayer}

object TaskDaoImplTest extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = {
    (suite("TaskDaoTest")(
      creatingTable,
      successfulInsertTask,
      successfulGetTasksByCreator,
      successfulUpdateTaskStatus,
      successfulSoftDeleteTask
    ) @@ before(initTable) @@ after(cleanTable) @@ sequential)
      .provideLayer(makeLayer)
  }

  private def cleanTable =
    for {
      xa <- ZIO.service[Transactor[Task]]
      _ <- sql" DELETE FROM tasks ".update.run.transact(xa)
    } yield ()

  def initTable =
    ZIO.serviceWithZIO[Transactor[Task]] { xa =>
      for {
        _ <- InitSchema("/tasks.sql", xa)
        _ = println("схема готова!")
      } yield ()
    }

  def makeLayer = ZLayer.make[TaskDao with Transactor[Task]](
    PostgresTestContainer.defaultSettings,
    PostgresTestContainer.postgresTestContainer,
    PostgresTestContainer.xa,
    TaskDaoImpl.live
  )

  def creatingTable = {
    test("check table exists") {
      for {
        xa <- ZIO.service[Transactor[Task]]
        result <- sql"""
                       SELECT table_name
                       FROM information_schema.tables
                       WHERE table_schema = 'public' AND table_name LIKE 'tasks';
                  """
          .query[String]
          .unique
          .transact(xa)
      } yield assertTrue(result == "tasks")
    }
  }

  def successfulInsertTask = {
    test("successful insert task") {
      val testTask = Help.defaultUsers.head
      val result = (for {
        dao <- ZIO.service[TaskDao]
        _ <- dao.createTask(testTask)
      } yield ())

      assertZIO(result)(isUnit)
    }
  }

  def successfulGetTasksByCreator = {
    test("successful get task by creator") {
      for {
        dao <- ZIO.service[TaskDao]
        xa <- ZIO.service[Transactor[Task]]
        _ <- Help.fillBase(xa)
        tasks <- dao.getBy(Filter.ByCreator(1))
      } yield assertTrue(tasks == Help.defaultUsers.take(2).toList)
    }
  }

  def successfulUpdateTaskStatus = {
    test("successful make task closed") {
      for {
        dao <- ZIO.service[TaskDao]
        xa <- ZIO.service[Transactor[Task]]
        _ <- Help.fillBase(xa)
        task <- dao.get(1)
        _ <- dao.updateTaskStatus(task.get.id, Status.Closed)
        newTask <- dao.get(1)
      } yield assertTrue(newTask.get.status == Status.Closed)
    }
  }

  def successfulSoftDeleteTask = {
    test("successful make task deleted") {
      for {
        dao <- ZIO.service[TaskDao]
        xa <- ZIO.service[Transactor[Task]]
        _ <- Help.fillBase(xa)
        task <- dao.get(1)
        _ <- dao.softDelete(task.get.id)
        newTask <- dao.get(1)
      } yield assertTrue(newTask.get.status == Status.Delete)
    }
  }

  object Help extends DoobieMapping {

    def fillBase(xa: Transactor[Task], tasks: Seq[UserTask] = defaultUsers): Task[Unit] =
      ZIO.foreachDiscard(tasks) { task =>
        insertUser(task).transact(xa)
      }

    val defaultTime = DateTime.parse("2024-01-01")

    val defaultUsers =
      Seq(
        UserTask(1, "task1", 1, "description", Status.Active, defaultTime, 1, 0.0, 0.0),
        UserTask(2, "task2", 1, "description", Status.Active, defaultTime, 1, 0.0, 0.0),
        UserTask(3, "task3", 3, "description", Status.Active, defaultTime, 1, 0.0, 0.0),
        UserTask(4, "task4", 4, "description", Status.Active, defaultTime, 1, 0.0, 0.0)
      )

    private def insertUser(task: UserTask) =
      sql"""
            INSERT INTO tasks (
              name, creator_id, description, status, created_at, involved_count, x_coord, y_coord
            )
            VALUES (
              ${task.name},
              ${task.creatorId},
              ${task.description},
              ${task.status.name},
              ${task.createdAt},
              ${task.involvedCount},
              ${task.xCoord},
              ${task.yCoord}
            )
         """.update.run

  }
}
