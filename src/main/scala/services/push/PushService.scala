package services.push

import models.dao.rating.Like
import models.dao.task.UserTask
import models.dao.task.UserTask.Status
import models.responses.PushResponse
import zio.Task

trait PushService {

  def getByUser(userId: Long): Task[Seq[PushResponse]]

  def sendPushWhenChangedStatus(newStatus: Status, task: UserTask): Task[Unit]

  def sendPushWhenGetLike(like: Like): Task[Unit]

  def sendPushWhenUserTakeYourTask(userWhoTakeId: Long, task: UserTask): Task[Unit]

}
