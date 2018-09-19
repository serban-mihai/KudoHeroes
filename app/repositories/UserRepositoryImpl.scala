package repositories

import javax.inject.Inject
import models.User
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.FailoverStrategy
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

//@Singleton
class UserRepositoryImpl @Inject()(reactiveMongoApi: ReactiveMongoApi) (implicit ec: ExecutionContext) extends UserRepository{

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
  /*override def findAll(): Future[List[User]] = {
    collection.flatMap(_.find(Json.obj())
      .cursor[User](ReadPreference.primary)
      .collect[List](Int.MaxValue, Cursor.ContOnError[List[User]]()))
  }

  override def findById(id: String) = {
    val s = Json.obj("id" -> id)
    collection.flatMap(_.find(s).one[User](ReadPreference.primary))
  }

  override def delete(id: String) = {
    for{
      d <- collection.flatMap(_.remove(Json.obj("id" -> id)))
    } yield {
      if (d.ok){
        d
      }
      else d
    }
  }

  override def update(user: User) = {
    val s = Json.obj("id" -> user.id)
    val usr = user
    for{
      _ <- collection
        .flatMap(_.update(s, usr))
      r <- findById(usr.id)
    } yield r
  }

  override def findByName(name: String): Future[Option[User]] = {
    val s = Json.obj("real_name" -> name)
    collection.flatMap(_.find(s).one[User](ReadPreference.primary))
  }*/

}
