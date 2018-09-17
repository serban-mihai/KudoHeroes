package models

import play.api.libs.json._
import reactivemongo.bson.BSONObjectID

import scala.util.Try

case class User(id: String,
                real_name: String,
                image_24: String,
                tacos: Int)

object User {
  implicit object BSONObjectIDFormat extends Format[BSONObjectID] {
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
  }
  implicit val userFormat = Json.format[User]
}
