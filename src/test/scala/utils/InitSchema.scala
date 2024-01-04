package utils

import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import org.apache.commons.io.IOUtils
import doobie.implicits._
import zio.interop.catz._
import zio.{Task, ZIO}

object InitSchema {

  private def readSql(path: String): ZIO[Any, Throwable, String] = {
    ZIO.scoped {
      ZIO
        .fromAutoCloseable(
          ZIO.attempt(getClass.getResourceAsStream(path).ensuring(_ != null, s"Resource $path not found"))
        )
        .flatMap(is => ZIO.attempt(IOUtils.toString(is, "utf-8")))
    }
  }

  def apply(path: String, xa: Transactor[Task]): Task[Unit] = {

    val schema: ZIO[Any, Throwable, String] = readSql(path)

    for {
      schema <- schema
      statements <- ZIO.attempt(schema.split(";\n"))
      _ <- ZIO.foreachDiscard(statements.toSeq)(s => Update0(s, None).run.transact(xa))
    } yield ()

  }
}