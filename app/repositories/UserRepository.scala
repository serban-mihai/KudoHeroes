package repositories

import models.User
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

trait UserRepository {

  def create(user: User): Future[Option[User]]

  def findById(id: String): Future[Option[User]]

  def findByName(name: String): Future[Option[User]]

  def delete(id: String): Future[WriteResult]

  def findAll(): Future[List[User]]

  def update(user: User): Future[Option[User]]

}
