package services

import javax.inject.{Inject, Singleton}
import models.User
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.FailoverStrategy
import reactivemongo.play.json.collection.JSONCollection
import repositories.UserRepository
import scala.concurrent.{ExecutionContext, Future}


//@Singleton
class UserServiceImpl @Inject() (reactiveMongoApi: ReactiveMongoApi, userRepository: UserRepository) (implicit ex: ExecutionContext) extends UserService {

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection("user", FailoverStrategy.default))
  }

  def create(user: User) = {
    userRepository.create(user)
  }

}
