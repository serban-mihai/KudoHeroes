package services

import reactivemongo.api.commands.WriteResult
import models.Message
import scala.concurrent.Future

trait MessageService {
  def create(message: Message): Future[Option[Message]]

  def findById(id: String): Future[Option[Message]]

  def findBySenderIdTs(sender: String, ts: String): Future[List[Message]]

  def findAll(): Future[List[Message]]

  def findByName(name: String): Future[Option[Message]]

  def delete(id: String): Future[WriteResult]

  def update(message: Message): Future[Option[Message]]

}
