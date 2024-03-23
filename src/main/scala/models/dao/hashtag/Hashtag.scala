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

    case object Culture extends Tag {
      def name: String = "culture"
    }

    case object Sport extends Tag {
      def name: String = "sport"
    }

    case object Health extends Tag {
      def name: String = "health"
    }


    def withName(name: String): Tag =
      name match {
        case "animal" => Animal
        case "nature" => Nature
        case "help-to-people" => HelpToPeople
        case "culture" => Culture
        case "sport" => Sport
        case "health" => Health
        case _ => throw new IllegalArgumentException(s"No tag with name = $name")
      }
  }

}
