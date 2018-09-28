package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Message(id: String, sender: String, receiver: List[String], text: String, tacos: Int, ts: String)

object Message {

  implicit def optionFormat[T: Format]: Format[Option[T]] = new Format[Option[T]]{
    override def reads(json: JsValue): JsResult[Option[T]] = json.validateOpt[T]

    override def writes(o: Option[T]): JsValue = o match {
      case Some(t) ⇒ implicitly[Writes[T]].writes(t)
      case None ⇒ JsNull
    }
  }
  implicit val messageReads: Reads[Message] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "sender").read[String] and
    (JsPath \ "receiver").read[List[String]] and
    (JsPath \ "text").read[String] and
      (JsPath \ "tacos").read[Int] and
      (JsPath \ "ts").read[String]
    ) (Message.apply _)

  implicit val messageWrites: OWrites[Message] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "sender").write[String] and
      (JsPath \ "receiver").write[List[String]] and
      (JsPath \ "text").write[String] and
      (JsPath \ "tacos").write[Int] and
        (JsPath \ "ts").write[String]
    ) (unlift(Message.unapply))

  implicit val messageFormat: OFormat[Message] = Json.format[Message]
  implicit val msgFormat: Format[Message] = Format(messageReads, messageWrites)
}