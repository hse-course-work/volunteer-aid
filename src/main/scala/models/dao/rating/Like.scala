package models.dao.rating

import org.joda.time.DateTime

case class Like(
    id: Long,
    userIdLikeFor: Long,
    taskId: Long,
    createdAt: DateTime)
