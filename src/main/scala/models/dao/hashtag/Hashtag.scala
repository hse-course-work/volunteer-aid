package models.dao.hashtag

import models.dao.hashtag.Hashtag.Tag

case class Hashtag(value: Tag, taskId: Long)

object Hashtag {

  sealed trait Tag {
    def name: String;
  }

  object Tag {
    case object Animal extends Tag {
      def name: String = "animal"
    }

    case object Nature extends Tag {
      def name: String = "nature"
    }

    case object HelpToPeople extends Tag {
      def name: String = "help-to-people"
    }

    def withName(name: String): Tag =
      name match {
        case "animal" => Animal
        case "nature" => Nature
        case "help-to-people" => HelpToPeople
        case _ => throw new IllegalArgumentException(s"No tag with name = $name")
      }
  }

}
