package utils

import pureconfig.ConfigSource
import pureconfig._
import pureconfig.error.ConfigReaderException
import zio.{Tag, ULayer, ZIO, ZLayer}

import scala.reflect.ClassTag

object PureConfig {

  def load[A: ClassTag: ConfigReader: Tag](fileName: String, at: String): ULayer[A] =
    ZLayer.succeed(
      ConfigSource.resources(fileName).at(at).load[A]
        .getOrElse(throw new IllegalArgumentException("Can not read from conf file!"))
    )

}
