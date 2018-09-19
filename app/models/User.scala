package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class User(id: String,
                real_name: String,
                image_24: String,
                tacos: Option[Int],
                isAdmin: Boolean,
                )

object User {
  /*implicit object BSONObjectIDFormat extends Format[BSONObjectID] {
    def writes(objectId: BSONObjectID): JsValue = JsString(objectId.stringify)
    def reads(json: JsValue): JsResult[BSONObjectID] = json match {
      case JsString(x) => {
        val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(x)
        if(maybeOID.isSuccess) JsSuccess(maybeOID.get) else {
          JsError("Expected BSONObjectID as JsString")
        }
      }
      case _ => JsError("Expected BSONObjectID as JsString")
    }
  }*/
  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]]{
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) ⇒ implicitly[Writes[T]].writes(t)
      case None ⇒ JsNull
    }
  }
  val userReads: Reads[User] =(
      (JsPath \ "id").read[String] and
      (JsPath \ "real_name").read[String] and
      (JsPath \ "image_24").read[String] and
      (JsPath \ "tacos").read[Option[Int]] and
      (JsPath \ "isAdmin").read[Boolean]
    ) (User.apply _)

  val userWrites: Writes[User] =(
      (JsPath \ "id").write[String] and
      (JsPath \ "real_name").write[String] and
      (JsPath \ "image_24").write[String] and
      (JsPath \ "tacos").write[Option[Int]] and
      (JsPath \ "isAdmin").write[Boolean]
    ) (unlift(User.unapply))

  implicit val movieFormat: Format[User] =
    Format(userReads, userWrites)
  implicit val userFormat = Json.format[User]
}
