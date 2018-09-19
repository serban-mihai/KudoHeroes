package controllers
import javax.inject._
import models.User
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.ws._
import play.api.mvc._
import services.UserService
import slack.api.SlackApiClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()
(cc: ControllerComponents, userService: UserService, ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  val token: String = "Ql4Ucll3hMbJE4WNVLJerd67" // replace your Slack API token here
  val apiClient: SlackApiClient = new SlackApiClient(token)
  // val history: Future[Channel] = apiClient.getChannelInfo("CCUH60SF9")(system = )
  // println(history.toString)
  def createUser() = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => Future.successful(BadRequest(Json.obj("status" -> "Invalid"))),
      user => {
        userService
          .create(user)
          .map(toJson(_))
          .map(Created(_))
      }
    )
  }
  /*def findById(id: String) = Action.async { implicit request =>
    userService
      .findById(id)
      .map(toJson(_))
      .map(Ok(_))
  }
  def findAllUser() = Action.async { implicit request =>
    userService
      .findAll()
      .map(toJson(_))
      .map(Ok(_))
  }
  def findByName(name: String) = Action.async { implicit request =>
    userService
      .findByName(name)
      .map(toJson(_))
      .map(Ok(_))
  }

  def deleteUser(id: String) = Action.async { implicit request =>
    userService
      .delete(id)
      .map(_ => Ok)
  }

  def updateUser() = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => Future.successful(BadRequest(Json.obj("status" -> "Invalid"))),
      user => {
        userService
          .update(user)
          .map(toJson(_))
          .map(Ok(_))
      }
    )
  }*/
}
