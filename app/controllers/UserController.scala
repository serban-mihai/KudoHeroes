package controllers

import akka.actor._
import javax.inject._
import models._
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc._
import services.UserService
import slack.api.SlackApiClient

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class UserController @Inject()
(cc: ControllerComponents, userService: UserService, ws: WSClient)(implicit ec: ExecutionContext) extends AbstractController(cc){


  def getUsers = Action.async { implicit request =>

    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher
    val token = "xoxa-2-435422739219-440042584230-438104368577-f02e4038043c178be6e78f5a7c8987f4"
    val client = SlackApiClient(token)


    for(
      usr <- client.listUsers()
    ) yield {
      val finalList = usr.map { u =>
        User(u.id, u.profile.get.real_name.getOrElse(""), u.profile.get.image_192, u.is_bot.getOrElse(true), false)
      }

      val dbUsers = for {

        usrr <- finalList
        if (usrr.is_bot == false && usrr.id != "USLACKBOT")

      } yield {

        val exist = Await.result(userService.findById(usrr.id), Duration.Inf)

        if (exist == None)
          userService.create(usrr)
        else
          userService.update(usrr)

      }

      Ok(Json.toJson(finalList))

    }
  }


  def createUser() = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => Future.successful(BadRequest(Json.obj("status" -> "Invalid"))),
      user => {
        userService
          .create(user)
          .map(Json.toJson(_))
          .map(Created(_))
      }
    )
  }

  def findById(id: String) = Action.async { implicit request =>
    userService
      .findById(id)
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def findAllUser() = Action.async { implicit request =>
    userService
      .findAll()
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def findByName(name: String) = Action.async { implicit request =>
    userService
      .findByName(name)
      .map(Json.toJson(_))
      .map(Ok(_))
  }

  def delete(id: String) = Action.async { implicit request =>
    userService
      .delete(id)
      .map(_ => Ok)
  }

  def update() = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      _ => Future.successful(BadRequest(Json.obj("status" -> "Invalid"))),
      user => {
        userService
          .update(user)
          .map(Json.toJson(_))
          .map(Ok(_))
      }
    )
  }

}

