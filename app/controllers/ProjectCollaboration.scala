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
  
  private var members = Map.empty[String, PushEnumerator[JsValue]]
  
  def receive = {
    case Enter(user) => {
      val channel = new PushEnumerator[JsValue]
      members = members + (user -> channel)
      sender ! Connected(channel)
    }
    case Update(user, update: JsValue) => {
      for ((_, channel) <- members/*.filterKeys(_ != user)*/) {
        channel push update
      }
    }
    case Leave(user) => members = members - user
  }
}

case class Enter(user: String)
case class Connected(channel: Enumerator[JsValue])
case class Update(user: String, update: JsValue)
case class Leave(user: String)

object ProjectCollaboration extends Authenticated {
  
  private var rooms = Map.empty[Project, ActorRef]
  
  def join(id: Int)(req: RequestHeader): Promise[(Iteratee[JsValue, _], Enumerator[JsValue])] = {
    (for {
      user <- findUser(req)
      project <- Project.find(id)
    } yield {
      val room = rooms.get(project) getOrElse {
        val room = Akka.system.actorOf(Props[ProjectCollaboration])
        rooms = rooms + (project -> room)
        room
      }
      (room ? (Enter(user), 1 second)).asPromise map {
        case Connected(channel) => {
          val iteratee = Iteratee.foreach[JsValue] { event =>
            room ! Update(user, event)
          }.mapDone { _ =>
            room ! Leave(user)
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
