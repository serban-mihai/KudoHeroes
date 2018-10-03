package services


import models.User
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future

trait UserService {

  def create(user: User): Future[Option[User]]

  def findById(id: String): Future[Option[User]]



  def findByListId(ids: List[String]): Future[List[User]]

  def findAll(): Future[List[User]]

  def findByName(name: String): Future[Option[User]]

  def delete(id: String): Future[WriteResult]

  def update(user: User): Future[Option[User]]

}
