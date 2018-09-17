package repositories

import models.User
import scala.concurrent.Future

trait UserRepository {

  def create(user: User): Future[Option[User]]

}
