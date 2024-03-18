package repositories.push

import models.dao.push.Push
import zio.Task

trait PushDao {

  def getUserPushes(userId: Long): Task[Seq[Push]]

  def addPushToUser(push: Push): Task[Unit]

}
