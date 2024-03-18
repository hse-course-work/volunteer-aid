package models.dao.push

import org.joda.time.DateTime

case class Push(
    id: Long,
    userIdTo: Long,
    taskIdFor: Long,
    message: String,
    createdAt: DateTime)
