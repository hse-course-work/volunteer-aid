package services.hashtag

import models.dao.hashtag.Hashtag
import models.dao.task.UserTask
import services.task.TaskService.TaskException
import zio.IO

trait HashtagService {

  def addHashtag(hashtag: Hashtag): IO[TaskException, Unit]

  def deleteHashtag(hashtag: Hashtag): IO[TaskException, Unit]

  def getTasksByHashtags(hashtags: Seq[String]): IO[TaskException, Seq[UserTask]]

}
