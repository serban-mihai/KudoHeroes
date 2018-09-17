package services


import models.User
import scala.concurrent.Future

trait UserService {

  def create(user: User): Future[Option[User]]

}
