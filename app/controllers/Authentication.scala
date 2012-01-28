package controllers

import play.api.mvc.{Action, Controller}
import play.api.mvc.Results._
import play.api.data.Form
import play.api.data.Forms._

object Authentication extends Controller {
  
  val homeRedirect = Redirect(routes.Projects.index)

  def login = Action {
    Ok(views.html.login(authForm))
  }
  
  def authenticate = Action { implicit request =>
    authForm.bindFromRequest.fold(
        errors => BadRequest(views.html.login(errors)),
        username => homeRedirect.withSession("username"->username)
    )
  }
  
  def logout = Action {
    homeRedirect.withNewSession
  }
  
  val authForm = Form(mapping("username" -> nonEmptyText)(identity)(Some(_)))
}