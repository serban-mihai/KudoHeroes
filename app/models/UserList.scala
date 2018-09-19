package models

import play.api.libs.json.Json

import scala.collection.mutable.ListBuffer

case class UserList(userList: ListBuffer[User])

object UserList{
  implicit val userList = Json.format[UserList]
}
