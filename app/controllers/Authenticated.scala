package controllers
import play.api.mvc.{Security, Result, Request, Action, AnyContent, RequestHeader}
import play.api.mvc.Results._

trait Authenticated {
  
  def authenticated(f: String => Request[AnyContent] => Result) = Security.Authenticated(
      findUser,
      _ => Redirect(routes.Authentication.login)
    ) { user =>
    Action(req => f(user)(req))
  }
  
  def findUser(request: RequestHeader): Option[String] = request.session.get("username")
}