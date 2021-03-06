package controllers

import akka.actor._
import akka.util.Timeout
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
      val channel = Enumerator.imperative[JsValue]()
      members = members + (user -> channel)
      sender ! Connected(channel)
    }
    case Update(user, update: JsValue) => {
      for ((_, channel) <- members) {
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

object ProjectCollaboration extends Authentication {
  
  implicit val timeout = Timeout(1 second)
  
  private var rooms = Map.empty[Project, ActorRef]
  
  def join(id: Int)(req: RequestHeader): Promise[(Iteratee[JsValue, _], Enumerator[JsValue])] = {
    (for {
      user <- findUser(req)
      project <- Project.find(id)
    } yield {
      val room = rooms.get(project) getOrElse {
        val room = Akka.system.actorOf(Props[ProjectCollaboration])
        rooms = rooms + (project -> room) // FIXME may I encounter race conditions?
        room
      }
      (room ? Enter(user)).asPromise map {
        case Connected(channel) => {
          val iteratee = Iteratee.foreach[JsValue] { event =>
            // Nothing is done
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
  
  def notify(project: Project, msg: Any) =
    rooms.get(project) map { _ ! msg }
}
