package models

import play.api.libs.json.Json

case class Message(text: String)
//{"type":"message","user":"UCTFR4RC2","text":"<@UCTCJA03V> :taco:","client_msg_id":"e0fbe180-3d71-4c89-8605-378e909a28bd","ts":"1536909775.000100"}

object Message {

  implicit val messageFormat = Json.format[Message]

}