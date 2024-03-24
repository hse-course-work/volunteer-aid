package services.hashtag

import models.dao.hashtag.Hashtag
import models.dao.task.UserTask
import models.requests.task.SearchByTagRequest
import services.task.TaskService.TaskException
import zio.IO

trait HashtagService {

  def addHashtag(hashtag: Hashtag): IO[TaskException, Unit]

  def deleteHashtag(hashtag: Hashtag): IO[TaskException, Unit]

  def getTasksByHashtags(searchRequest: SearchByTagRequest): IO[TaskException, Map[String, Seq[UserTask]]]

}
