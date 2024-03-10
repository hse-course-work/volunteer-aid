package services.hashtag

import models.dao.hashtag.Hashtag
import repositories.hashtags.HashtagDao
import repositories.task.TaskDao
import services.task.TaskService.TaskException
import services.task.TaskService.TaskException.{InternalError, TaskNotFound}
import zio.{&, IO, URLayer, ZIO, ZLayer}

class HashtagServiceImpl(hashtagDao: HashtagDao, taskDao: TaskDao) extends HashtagService {
  def addHashtag(hashtag: Hashtag): IO[TaskException, Unit] =
    for {
      _ <- checkTaskExist(hashtag.taskId)
      _ <- hashtagDao.addHashtag(hashtag)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  def deleteHashtag(hashtag: Hashtag): IO[TaskException, Unit] =
    for {
      _ <- checkTaskExist(hashtag.taskId)
      _ <- hashtagDao.deleteHashtag(hashtag)
        .catchAll(e => ZIO.fail(InternalError(e)))
    } yield ()

  private def checkTaskExist(taskId: Long): IO[TaskException, Unit] = {
    taskDao.get(taskId)
      .catchAll(e => ZIO.fail(InternalError(e)))
      .flatMap {
        case Some(_) => ZIO.unit
        case None => ZIO.fail(TaskNotFound(taskId))
      }

  }
}

object HashtagServiceImpl {

  val live: URLayer[HashtagDao & TaskDao, HashtagService] =
    ZLayer.fromFunction(new HashtagServiceImpl(_, _))

}
