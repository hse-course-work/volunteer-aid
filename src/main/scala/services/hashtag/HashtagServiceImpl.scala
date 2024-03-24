package services.hashtag

import models.dao.hashtag.Hashtag
import models.dao.hashtag.Hashtag.Tag
import models.dao.task.UserTask
import models.requests.task.SearchByTagRequest
import repositories.hashtags.HashtagDao
import repositories.task.TaskDao
import services.hashtag.HashtagServiceImpl.DefaultRadiusMeters
import services.task.TaskService.TaskException
import services.task.TaskService.TaskException.{BadRequest, InternalError, TaskNotFound}
import zio.{&, IO, URLayer, ZIO, ZLayer}

class HashtagServiceImpl(hashtagDao: HashtagDao, taskDao: TaskDao) extends HashtagService {

  def addHashtag(hashtag: Hashtag): IO[TaskException, Unit] =
    for {
      _ <- checkTaskExist(hashtag.taskId)
      _ <- hashtagDao
        .addHashtag(hashtag)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def deleteHashtag(hashtag: Hashtag): IO[TaskException, Unit] =
    for {
      _ <- checkTaskExist(hashtag.taskId)
      _ <- hashtagDao
        .deleteHashtag(hashtag)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  private def checkTaskExist(taskId: Long): IO[TaskException, Unit] =
    taskDao
      .get(taskId)
      .catchAll(e => ZIO.fail(InternalError(e)))
      .flatMap {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(TaskNotFound(taskId))
      }

  def getTasksByHashtags(searchRequest: SearchByTagRequest): IO[TaskException, Map[String, Seq[UserTask]]] = {
    val hashtags = {
      if (searchRequest.tags.nonEmpty) searchRequest.tags
      else
        Seq(Tag.Animal.name, Tag.Sport.name, Tag.Health.name, Tag.Culture.name, Tag.HelpToPeople.name, Tag.Nature.name)
    }
    for {
      tags <- ZIO
        .attempt(hashtags.map(Tag.withName))
        .catchAll(e => ZIO.fail(BadRequest(e)))
      tasksWithTag <- ZIO.foreach(tags)(tag =>
        hashtagDao
          .getByTag(tag, searchRequest.curX, searchRequest.curY, searchRequest.radius.getOrElse(DefaultRadiusMeters))
          .catchAll(e => ZIO.fail(InternalError(e)))
          .map(tasks => tag.name -> tasks.map(_.taskId))
      )
      taskIdsByHashtag = tasksWithTag.toMap
      tasksByHashtags <- ZIO.foreach(taskIdsByHashtag) { case (tag, taskIds) =>
        ZIO
          .foreach(taskIds)(id =>
            taskDao
              .get(id)
              .catchAll(e => ZIO.fail(InternalError(e)))
          )
          .map(tasks => tag -> tasks.filter(_.nonEmpty).map(_.get))
      }
    } yield tasksByHashtags
  }

}

object HashtagServiceImpl {

  val live: URLayer[HashtagDao & TaskDao, HashtagService] =
    ZLayer.fromFunction(new HashtagServiceImpl(_, _))

  private val DefaultRadiusMeters = 10000

}
