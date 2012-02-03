package controllers

import play.api.mvc.{Action, Controller, Request, AnyContent, Result, Security, RequestHeader}
import play.api.mvc.Results._
import play.api.data.Form
import play.api.data.Forms._

trait Authentication {
  def authenticated(f: String => Request[AnyContent] => Result) = Security.Authenticated(
      findUser,
      _ => Redirect(routes.Authentication.login)
    ) { user =>
    Action(req => f(user)(req))
  }
  
  def findUser(request: RequestHeader): Option[String] = request.session.get(Authentication.USER_KEY)
}

object Authentication extends Controller {
  
  val homeRedirect = Redirect(routes.Projects.index)
  val USER_KEY = "username"

  def login = Action {
    Ok(views.html.login(authForm))
  }
  
  def authenticate = Action { implicit request =>
    authForm.bindFromRequest.fold(
        errors => BadRequest(views.html.login(errors)),
        username => homeRedirect.withSession(USER_KEY->username)
    )
  }
  
  def logout = Action {
    homeRedirect.withNewSession
  }
  
  val authForm = Form(mapping("username" -> nonEmptyText)(identity)(Some(_)))
}