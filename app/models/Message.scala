package models

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Message(id: String, sender: String, receiver: String, text: String, tacos: Option[Int], ts: String)
//{"type":"message","user":"UCTFR4RC2","text":"<@UCTCJA03V> :taco:","client_msg_id":"e0fbe180-3d71-4c89-8605-378e909a28bd","ts":"1536909775.000100"}

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
    (JsPath \ "receiver").read[String] and
    (JsPath \ "text").read[String] and
      (JsPath \ "tacos").read[Option[Int]] and
      (JsPath \ "ts").read[String]
    ) (Message.apply _)

  implicit val messageWrites: OWrites[Message] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "sender").write[String] and
      (JsPath \ "receiver").write[String] and
      (JsPath \ "text").write[String] and
      (JsPath \ "tacos").write[Option[Int]] and
        (JsPath \ "ts").write[String]
    ) (unlift(Message.unapply))

  implicit val messageFormat: OFormat[Message] = Json.format[Message]
  implicit val msgFormat: Format[Message] = Format(messageReads, messageWrites)
}