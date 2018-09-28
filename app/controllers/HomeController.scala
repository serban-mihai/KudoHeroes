package controllers

import javax.inject._
import models.User
import play.api.mvc._
import services.UserService

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
/*@Singleton*/
class HomeController @Inject()(cc: ControllerComponents, userService: UserService) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(id: String) = Action {
    // Async possibly useless, change ASAP!
    val user: Option[User] = Await.result(userService.findById(id), Duration(5, "seconds"))
    val users: List[User] = Await.result(userService.findAll(), Duration(5, "seconds"))
    Ok(views.html.index("Your new application is ready.")(user)(users))
  }
}
