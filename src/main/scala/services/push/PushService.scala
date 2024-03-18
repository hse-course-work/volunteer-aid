package services.push

import models.dao.push.Push
import models.dao.rating.Like
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import zio.Task

trait PushService {

  def getByUser(userId: Long): Task[Seq[Push]]

  def sendPushWhenChangedStatus(newStatus: Status, task: UserTask): Task[Unit]

  def sendPushWhenGetLike(like: Like): Task[Unit]

  def sendPushWhenUserTakeYourTask(userWhoTakeId: Long, task: UserTask): Task[Unit]

}
