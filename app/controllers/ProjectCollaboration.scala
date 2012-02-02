package controllers

import akka.actor._
import akka.util.duration._

import models.Project

import play.api.Play.current

import play.api.libs.concurrent._
import play.api.libs.iteratee._
import play.api.libs.json._

import play.api._
import play.api.mvc.RequestHeader

class ProjectCollaboration extends Actor {
  
  var members = Map.empty[String, PushEnumerator[JsValue]]
  
  def receive = {
    case Join(user) => sender ! Connected(null)
  }
  
}

case class Join(user: String)
case class Connected(channel: Enumerator[JsValue])

object ProjectCollaboration extends Authenticated {
  
  def join(id: Int)(req: RequestHeader): Promise[(Iteratee[JsValue, _], Enumerator[JsValue])] = {
    (for {
      user <- findUser(req)
      project <- Project.find(id)
    } yield {
      val room = Akka.system.actorOf(Props[ProjectCollaboration])
      (room ? (Join(user), 1 second)).asPromise map {
        case Connected(channel) => {
          val iteratee = Iteratee.foreach[JsValue] { event =>
            
          }
          (iteratee, channel)
        }
      }
    }) getOrElse {
      Promise.pure(
          Done[JsValue, Unit]((), Input.EOF),
          Enumerator[JsValue](JsObject(List("error"->JsString("No.")))) andThen Enumerator.enumInput(Input.EOF)
      )
    }
  }
}
