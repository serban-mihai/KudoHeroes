package models

import play.api.libs.json._

case class User(id: String,
                real_name: String,
                image_24: String,
                is_bot: Boolean,
                tacos: Option[Int])

object User {

  implicit val userFormat = Json.format[User]

}
