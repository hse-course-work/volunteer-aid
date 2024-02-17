package utils

import java.util.Base64

object PasswordHelper {

  def decode(password: String): String =
    Base64.getEncoder.encodeToString(password.getBytes("UTF-8"))

  def encode(hashPassword: String): String =
    new String(Base64.getDecoder.decode(hashPassword), "UTF-8")

}
