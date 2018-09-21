package controllers


import akka.actor._
import javax.inject._
import models._
import play.api.libs.ws._
import play.api.mvc._
import services.UserService
import slack.api.SlackApiClient
import scala.collection.mutable

class UserController @Inject()
(cc: ControllerComponents, userService: UserService, ws: WSClient) extends AbstractController(cc){




  def countWords(text: String) = {
    val counts = mutable.Map.empty[String, Int].withDefaultValue(0)
    for (rawWord <- text.split("[:,!.]+")) {
      val word = rawWord
      counts(word) += 1
    }
    counts
  }

  def extractTaco(message: models.Message) = {

    val reg = "[:A-z0-9]+".r
    val list = reg.findAllIn(message.text).toList
    val tacos = countWords(list.tail.toString()).filter(_._1.equals("taco"))
    val counter = tacos.get("taco").getOrElse(0)

    (list.head, counter)

  }

  def userInfo = Action.async { implicit request =>

    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher
    val token = "xoxa-2-435422739219-440042584230-438104368577-f02e4038043c178be6e78f5a7c8987f4"
    val client = SlackApiClient(token)


    for {
      msg <- client.getChannelHistory("CCV15TEKY", None, None, None, None)
      msgs = msg.messages.map { e => e.as[models.Message] }
      usr <- client.listUsers()

      tacos2 = msgs.map {
        m => extractTaco(m)
      }
      finaltaco = tacos2.groupBy(_._1).map(e => e._1 -> e._2.map(a=>a._2).sum)

    } yield {
      val finalList = usr.map { u =>
        User(u.id, u.profile.get.real_name.getOrElse(""), u.profile.get.image_24, u.is_bot.getOrElse(true),Option(finaltaco.get(u.id).getOrElse(0)))
      }

      for{
        usr <- finalList
        if(usr.is_bot == false)
      } userService.create(usr)


      Ok(finalList.toString())
    }
  }
}



