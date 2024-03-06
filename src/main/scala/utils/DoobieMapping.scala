package utils

import doobie.util.meta.{MetaConstructors, SqlMetaInstances}
import doobie.{Get, Put}
import org.joda.time.DateTime
import java.sql.Timestamp

trait DoobieMapping extends MetaConstructors with SqlMetaInstances {

  implicit val putDateTime: Put[DateTime] =
    Put[Timestamp].tcontramap[DateTime](date => new Timestamp(date.getMillis))

  implicit val getDateTime: Get[DateTime] =
    Get[Timestamp].map(datetime => new DateTime(datetime.getTime))

}
