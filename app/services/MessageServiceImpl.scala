package services

import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import models.Message
import reactivemongo.api.FailoverStrategy
import reactivemongo.play.json.collection.JSONCollection
import repositories.MessageRepository

import scala.concurrent.{ExecutionContext, Future}

class MessageServiceImpl @Inject()(reactiveMongoApi: ReactiveMongoApi, messageRepository: MessageRepository)(implicit ex: ExecutionContext) extends MessageService {
  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection("user", FailoverStrategy.default))
  }

  def create(message: Message) = {
    messageRepository.create(message)
  }

  override def findById(messageID: String): Future[Option[Message]] = {
    messageRepository.findById(messageID)
  }
  override def findAll(): Future[List[Message]] = {
    for {
      u <- messageRepository.findAll()
    } yield u
  }
  override def delete(id: String) = {
    messageRepository.delete(id)
  }

  override def update(message: Message) = {
    messageRepository.update(message)
  }

  override def findByName(name: String): Future[Option[Message]] = {
    messageRepository.findByName(name)
  }

}
