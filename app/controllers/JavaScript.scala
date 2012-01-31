package controllers

import play.api.mvc.{Controller, Results}
import play.api.Routes
import Results._

object JavaScript extends Controller with Authenticated {
  def routes = authenticated { user => request =>
    import _root_.controllers.routes.javascript._
    Ok(
        Routes.javascriptRouter("routes")(
            Tasks.move
            )
        ).as("text/javascript")
  }
}