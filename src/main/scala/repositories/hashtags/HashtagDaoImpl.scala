package repositories.hashtags

import doobie.{ConnectionIO, Read}
import doobie.util.transactor.Transactor
import doobie.implicits._
import models.dao.hashtag.Hashtag
import models.dao.hashtag.Hashtag.Tag
import repositories.hashtags.HashtagDaoImpl.Sql
import zio.interop.catz._
import zio.{Task, URLayer, ZLayer}

class HashtagDaoImpl(master: Transactor[Task]) extends HashtagDao {
  def addHashtag(hashtag: Hashtag): Task[Unit] =
    Sql
      .insert(hashtag)
      .transact(master)
      .unit

  def deleteHashtag(hashtag: Hashtag): Task[Unit] =
    Sql
      .delete(hashtag)
      .transact(master)
      .unit

  def getByTag(tag: Tag): Task[Seq[Hashtag]] =
    Sql
      .getByTag(tag)
      .transact(master)

}

object HashtagDaoImpl {

  val live: URLayer[Transactor[Task], HashtagDao] =
    ZLayer.fromFunction(new HashtagDaoImpl(_))

  object Sql {

    def insert(hashtag: Hashtag): ConnectionIO[Int] =
      sql"""
            INSERT INTO task_hashtags (
              value, task_id
            ) VALUES (
              ${hashtag.value.name},
              ${hashtag.taskId}
            )
         """
        .update
        .run

    def delete(hashtag: Hashtag): ConnectionIO[Int] =
      sql"""
            DELETE FROM task_hashtags
            WHERE task_id = ${hashtag.taskId}
            AND value = ${hashtag.value.name}
         """
        .update
        .run

    def getByTag(tag: Tag): ConnectionIO[Seq[Hashtag]] =
      sql"""
            SELECT * FROM task_hashtags
            WHERE value = ${tag.name}
         """
        .query[Hashtag]
        .to[Seq]

    implicit val readHashtag: Read[Hashtag] =
      Read[(String, Long)].map {
        case (value, id) => new Hashtag(Tag.withName(value), id)
      }
  }

}
