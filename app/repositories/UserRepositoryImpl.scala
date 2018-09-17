package repositories

import javax.inject.Inject
import models.User
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.FailoverStrategy
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class  UserRepositoryImpl @Inject()(reactiveMongoApi: ReactiveMongoApi) (implicit ec: ExecutionContext) extends UserRepository{

  def collection: Future[JSONCollection] = {
    reactiveMongoApi.database.map(_.collection("user", FailoverStrategy.default))
  }

  def create(user: User) = {
    val newID = user.id
    val r = user.copy(
      id = newID
    )
    val newUser = for {
      _ <- collection.flatMap(_.insert[User](r))
    } yield Some(r)
    newUser
  }

}
