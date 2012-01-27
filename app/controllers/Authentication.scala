package controllers

import play.api.mvc.{Action, Controller}
import play.api.mvc.Results._
import play.api.data.Form
import play.api.data.Forms._

object Authentication extends Controller {

  def login = Action {
    Ok(views.html.login(authForm))
  }
  
  def authenticate = Action { implicit request =>
    authForm.bindFromRequest.fold(
        errors => BadRequest(views.html.login(errors)),
        username => Redirect(routes.BluePrintr.index).withSession("username"->username)
    )
  }
  
  def logout = Action {
    Redirect(routes.BluePrintr.index).withNewSession
  }
  
  val authForm = Form(mapping("username" -> text)(identity)(Some(_)))
}