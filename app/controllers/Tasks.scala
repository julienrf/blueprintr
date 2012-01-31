package controllers

import play.api.mvc.{Controller, Results}
import Results._
import play.api.libs.json.Json
import models.Task
import registry.Transaction

object Tasks extends Controller with Authenticated with Transaction {
  
  implicit val taskJson = models.json.taskJson
  
  def move(id: Int, startTime: Int) = authenticated { user => implicit request =>
    Task.find(id) match {
      case Some(task) => {
        atomic {
          task.move(startTime)
        }
        Ok(Json.toJson(task))
      }
      case None => BadRequest
    }
  }
}