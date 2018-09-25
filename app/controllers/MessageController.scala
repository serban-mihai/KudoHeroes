package controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import services.MessageService
import models.Message
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MessageController @Inject()
(cc: ControllerComponents, messageService: MessageService, ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  def createMessage() = Action.async(parse.json) { implicit request =>
    request.body.validate[Message].fold(
      _ => Future.successful(BadRequest(Json.obj("status" -> "Invalid"))),
      message => {
        messageService
          .create(message)
          .map(Json.toJson(_))
          .map(Created(_))
      }
    )
  }

  def findById(id: String) = Action.async { implicit request =>
    messageService
      .findById(id)
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def findAllMessage() = Action.async { implicit request =>
    messageService
      .findAll()
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def findByName(name: String) = Action.async { implicit request =>
    messageService
      .findByName(name)
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def delete(id: String) = Action.async { implicit request =>
    messageService
      .delete(id)
      .map(_ => Ok)
  }

  def update() = Action.async(parse.json) { implicit request =>
    request.body.validate[Message].fold(
      _ => Future.successful(BadRequest(Json.obj("status" -> "Invalid"))),
      message => {
        messageService
          .update(message)
          .map(Json.toJson(_))
          .map(Ok(_))
      }
    )
  }
}
