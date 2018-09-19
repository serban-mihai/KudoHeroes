package services

import javax.inject.{Inject, Singleton}
import models.User
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.FailoverStrategy
import reactivemongo.play.json.collection.JSONCollection
import repositories.UserRepository

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserServiceImpl @Inject() (reactiveMongoApi: ReactiveMongoApi, userRepository: UserRepository) (implicit ex: ExecutionContext) extends UserService {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection("user", FailoverStrategy.default))
  }

  def create(user: User) = {
    userRepository.create(user)
  }
  /*override def findAll(): Future[List[User]] = {
    for {
      u <- userRepository.findAll()
    } yield u
  }
  override def delete(id: String) = {
    userRepository.delete(id)
  }

  override def update(user: User) = {
    userRepository.update(user)
  }

  override def findById(userID: String): Future[Option[User]] = {
    userRepository.findById(userID)
  }

  override def findByName(name: String): Future[Option[User]] = {
    userRepository.findByName(name)
  }*/

}
