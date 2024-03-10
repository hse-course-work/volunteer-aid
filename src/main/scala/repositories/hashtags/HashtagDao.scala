package repositories.hashtags

import models.dao.hashtag.Hashtag
import models.dao.hashtag.Hashtag.Tag
import zio.Task

trait HashtagDao {

  def addHashtag(hashtag: Hashtag): Task[Unit]

  def deleteHashtag(hashtag: Hashtag): Task[Unit]

  def getByTag(tag: Tag): Task[Seq[Hashtag]]

}
