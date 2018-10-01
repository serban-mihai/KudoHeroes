package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{MessageService, UserService}
import models.{Message, MessageList}
import org.joda.time.DateTime
import slack.api.SlackApiClient

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class MessageController @Inject()
(cc: ControllerComponents, messageService: MessageService, ws: WSClient, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getTime(ts: Double) = {

    val dt = new DateTime(ts.toLong * 1000)
    dt

  }

  def countWords(text: String) = {
    val counts = mutable.Map.empty[String, Int].withDefaultValue(0)
    for (rawWord <- text.split("[:,!.]+")) {
      val word = rawWord
      counts(word) += 1
    }
    counts
  }


 /* def extractTaco(message: models.MessageList): Future[(List[String], Int)] = {

    val reg = "[:A-z0-9]+".r
    val list = reg.findAllIn(message.text).toList
    val tacos = countWords(list.tail.toString()).filter(_._1.equals("taco"))
    val counter = tacos.get("taco").getOrElse(0)

    for {
      users <- userService.findByListId(list)
      ids = users.map(_.id)
    } yield (ids,counter)
  }*/
 def extractTaco(message: models.MessageList) = {

   val reg = "[:A-z0-9]+".r
   val list = reg.findAllIn(message.text).toList
   val tacos = countWords(list.tail.toString()).filter(_._1.equals("taco"))
   val counter = tacos.get("taco").getOrElse(0)

   val userList = for {
     users <- userService.findByListId(list)
     ids = users.map(_.id)
   } yield ids

   (userList, counter)
 }


  def getDayOfTaco = Action { implicit request =>

    val listOfAllMessages = messageService.findAll()

    val usersMessages = for{

      msgs <- listOfAllMessages
    } yield {
      val listUsrMsg = msgs.groupBy(_.sender)

      val listUsrMsgs = listUsrMsg.map{list =>
        val map = list._2.map{msg =>
          (getTime(msg.ts.toDouble).dayOfYear().get, msg)
        }.groupBy(_._1).map{m =>
          (m._1, m._2.map(_._2))
        }
        (list._1, map)
      }.toList

      listUsrMsgs

    }

    for{
      msgList <- usersMessages.map(x => x.map(c => c._2))
    } yield{
            val msg = msgList.map(m => m.get(_))

    }

    println("Messages: ========================== ")

    for(msg <- usersMessages) msg.map(usr => println(usr))

    Ok("Oki Doki!")

  }

  //Put in database all messages from channel who have <<taco>>

  def getMessages = Action { implicit request =>

    implicit val system = ActorSystem("slack")
    implicit val ec = system.dispatcher
    val token = "xoxa-2-435422739219-440042584230-438104368577-f02e4038043c178be6e78f5a7c8987f4"
    val client = SlackApiClient(token)

    val message = Await.result(client.getChannelHistory("CCV15TEKY", None, None, None, None), Duration.Inf)
    println("Messages: " + message)


    val messages = for (
      msg <- client.getChannelHistory("CCV15TEKY", None, None, None, None)
    ) yield{
      msg.messages
    }

    implicit val msgListJson = Json.format[MessageList]

    val msgs = for(m <- messages) yield {
      m.flatMap(temp => temp.validate[MessageList] match {
        case JsSuccess(result, _) => Some(result)
        case JsError(err) =>
          //println("ce am vrut sa vedem: " + temp)
          None
      })
    }

   /* val finalMessages = for {
      msg <- msgs
      nrTacos <- Future.sequence(msg.map(m => extractTaco(m).map(t => (m.user, t))))
    } yield {
      msg.map { m =>
        val (listIds, counter) = nrTacos.find(t => t._1.equals(m.user)).get._2
        Message(m.client_msg_id,
          m.user,
          listIds,
          m.text,
          counter,
          m.ts)
      }
    }*/

 /*   val finalMessages = for {
      msg <- msgs
      nrTacos = msg.map(m => extractTaco(m))
      t <- nrTacos.map(e => e._1)
    } yield {
      msg.map { m =>
        Message(m.client_msg_id,
          m.user,
          nrTacos._1,
          m.text,
          nrTacos._2,
          m.ts)
      }
    }

    finalMessages.map(m => m.map{t =>

        messageService.create(t)

    })*/

    Ok("Ok")
  }



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
