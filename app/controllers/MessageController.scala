package controllers

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{MessageService, UserService}
import models.{Message, UserMessage}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import slack.api.SlackApiClient

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

@Singleton
class MessageController @Inject()
(cc: ControllerComponents, messageService: MessageService, ws: WSClient, userService: UserService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getTime(ts: Double) = {

    val dt = new DateTime(ts.toLong * 1000)
    dt

  }

  def getDate(ts: Long) = {

    val dtFormatter = DateTimeFormat.forPattern("dd-MM-yyyy")
    val res = dtFormatter.print(ts * 1000)
    res

  }

  def countWords(text: String) = {
    val counts = mutable.Map.empty[String, Int].withDefaultValue(0)
    for (rawWord <- text.split("[:,!.]+")) {
      val word = rawWord
      counts(word) += 1
    }
    counts
  }


 def extractUserMsgList(message: models.UserMessage) = {

   val reg = "[:A-z0-9]+".r
   val list = reg.findAllIn(message.text).toList

   val listId = for(users <- userService.findByListId(list)) yield users.map(_.id)
   Await.result(listId, Duration(1, "seconds"))
 }

  def extractTaco(message: models.UserMessage) = {

    val reg = "[:A-z0-9]+".r
    val list = reg.findAllIn(message.text).toList
    val tacos = countWords(list.toString()).filter(_._1.equals("taco"))
    val counter = tacos.get("taco").getOrElse(0)

    counter
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

  //Testing get message by sender id and ts


  def getTest = Action { implicit request =>

    val userMessage = messageService.findBySenderIdTs("UCTFR4RC2","20-09-2018")

    val s = for(smsg <- userMessage) yield smsg.map(r => r.tacos).seq.reduceLeft(_ + _)

    var validMessages = new ListBuffer[Message]()

    var tacos = 0

    val result =
      for{
        messages <- userMessage
      } yield {

        messages.map { msg =>
          if (tacos + msg.tacos <= 5) {
            tacos = tacos + msg.tacos
            validMessages += msg
          } else
            None
        }
        validMessages
      }

    userMessage.map(m => println(m))

    println("//================================//")

    result.map(msg => println("Valid Messages: " + msg))

    /*val o = for{
      msgs <- result
    } yield{
      msgs.groupBy(_.receiver).map{ usr =>
        val re = usr._2.groupBy(_.tacos)
        (usr._1, re.map(e.))
        }
      }
    }*/



    Ok("Ok")


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

    implicit val msgListJson = Json.format[UserMessage]

    val futureMsgs = for(m <- messages) yield {
      m.flatMap(temp => temp.validate[UserMessage] match {
        case JsSuccess(result, _) => Some(result)
        case JsError(err) =>
          //println("ce am vrut sa vedem: " + temp)
          None
      })
    }



    val finalMessages = for (
      msgs <- futureMsgs
    ) yield {
      msgs.map(msg =>
        Message(msg.client_msg_id,
          msg.user,
          extractUserMsgList(msg),
          msg.text,
          extractTaco(msg),
          getDate(msg.ts.toDouble.toLong)))
    }

    for(
      mess <- finalMessages
    ) mess.map{ms =>
      if(ms.receiver.length * ms.tacos <= 5 && ms.receiver.length * ms.tacos  >= 1 && !ms.text.contains("USLACKBOT"))
        messageService.create(ms)
      else
        None
    }


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

  def findBySenderId(sender: String, ts: String) = Action.async { implicit request =>
    messageService
      .findBySenderIdTs(sender, ts)
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
