package controllers
import play.api.mvc.{Security, Result, Request, Action, AnyContent}
import play.api.mvc.Results._

trait Authenticated {
  def authenticated(f: String => Request[AnyContent] => Result) = Security.Authenticated(
      req => req.session.get("username"),
      _ => Redirect(routes.Authentication.login)
    ) { user =>
    Action(req => f(user)(req))
  }
}