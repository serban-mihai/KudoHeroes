package repositories

import reactivemongo.api.commands.WriteResult
import models.Message
import scala.concurrent.Future

trait MessageRepository {

  def create(message: Message): Future[Option[Message]]

  def findById(id: String): Future[Option[Message]]

  def findByName(name: String): Future[Option[Message]]

  def delete(id: String): Future[WriteResult]

  def findAll(): Future[List[Message]]

  def update(message: Message): Future[Option[Message]]

}
