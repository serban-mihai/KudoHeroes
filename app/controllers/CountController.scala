package controllers

import javax.inject._
import play.api.mvc._
import services.Counter

/**
 * This controller demonstrates how to use dependency injection to
 * bind a component into a controller class. The class creates an
 * `Action` that shows an incrementing count to users. The [[Counter]]
 * object is injected by the Guice dependency injection system.
 */
@Singleton
class CountController @Inject() (cc: ControllerComponents,
                                 counter: Counter) extends AbstractController(cc) {

  /**
   * Create an action that responds with the [[Counter]]'s current
   * count. The result is plain text. This `Action` is mapped to
   * `GET /count` requests by an entry in the `routes` config file.
   */
  def count = Action {

    //Ok("Works")
    /*implicit val sys = ActorSystem.apply()
    val token = "xoxa-2-435422739219-440042584230-438104368577-f02e4038043c178be6e78f5a7c8987f4"
    val apiClient = SlackApiClient(token)
    val res = Await.result(apiClient.getChannelHistory("CCV15TEKY", None, None, None, None), Duration.Inf)
    val formatJs = res.messages
    println(res.messages.toString())*/

    Ok("Ok")
  }

}
