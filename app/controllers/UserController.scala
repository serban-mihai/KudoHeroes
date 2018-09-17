package controllers
import scala.concurrent.{ExecutionContext, Future}
import services.UserService
import javax.inject._
import models.User
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.ws._
import play.api.mvc._
import slack.api.SlackApiClient

@Singleton
class UserController @Inject()
(cc: ControllerComponents, userService: UserService, ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  //val token: String = "generated-token" // replace your Slack API token here
  //val apiClient: SlackApiClient




}
