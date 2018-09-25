package repositories

import javax.inject.Inject
import models.Message
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import reactivemongo.api.{Cursor, FailoverStrategy, ReadPreference}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

// @Singleton
class MessageRepositoryImpl @Inject()(reactiveMongoApi: ReactiveMongoApi) (implicit ec: ExecutionContext) extends MessageRepository {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection("message", FailoverStrategy.default))
  }

  def create(message: Message) = {
    val newID = message.id
    val r = message.copy(
      id = newID
    )
    val newMessage = for {
      _ <- collection.flatMap(_.insert[Message](r))
    } yield Some(r)
    newMessage
  }

  override def findById(id: String): Future[Option[Message]] = {
    collection.flatMap(_.find(Json.obj("id" -> id)).one[Message](ReadPreference.primary))
  }

  override def findAll(): Future[List[Message]] = {
    collection.flatMap(_.find(Json.obj()).sort(Json.obj("tacos" -> -1))
      .cursor[Message](ReadPreference.primary)
      .collect[List](Int.MaxValue, Cursor.ContOnError[List[Message]]()))
  }

  override def delete(id: String) = {
    for{
      d <- collection.flatMap(_.remove(Json.obj("id" -> id)))
    } yield {
      if (d.ok){
        d
      }
      else d
    }
  }

  override def update(message: Message) = {
    val s = Json.obj("id" -> message.id)
    val msg = message
    for{
      _ <- collection
        .flatMap(_.update(s, msg))
      r <- findById(msg.id)
    } yield r
  }

  override def findByName(name: String): Future[Option[Message]] = {
    val s = Json.obj("sender" -> name)
    collection.flatMap(_.find(s).one[Message](ReadPreference.primary))
  }

}
