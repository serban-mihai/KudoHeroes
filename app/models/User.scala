package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class User(id: String,
                real_name: String,
                image_192: String,
                is_bot: Boolean,
                isAdmin: Boolean)

object User {
  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]]{
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) ⇒ implicitly[Writes[T]].writes(t)
      case None ⇒ JsNull
    }
  }

  implicit val userReads: Reads[User] =(
      (JsPath \ "id").read[String] and
      (JsPath \ "real_name").read[String] and
      (JsPath \ "image_192").read[String] and
        (JsPath \ "is_bot").read[Boolean] and
      (JsPath \ "isAdmin").read[Boolean]
    ) (User.apply _)

  implicit val userWrites: OWrites[User] =(
      (JsPath \ "id").write[String] and
      (JsPath \ "real_name").write[String] and
      (JsPath \ "image_192").write[String] and
        (JsPath \ "is_bot").write[Boolean] and
      (JsPath \ "isAdmin").write[Boolean]
    ) (unlift(User.unapply))

  implicit val movieFormat: Format[User] =
    Format(userReads, userWrites)
  implicit val userFormat: OFormat[User] = Json.format[User]
}
