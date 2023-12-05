import zio.prelude.Newtype

package object models {

  object UserId extends Newtype[Long]
  type UserId = UserId.Type

  object Email extends Newtype[String]
  type Email = Email.Type

  object Password extends Newtype[String]
  type Password = Password.Type

  object Description extends Newtype[String]
  type Description = Description.Type
}
