package controllers

import play.api.mvc.{Controller, Results}
import Results._
import models.Task
import java.sql.Time

object Tasks extends Controller with Authenticated {
  
  def move(id: Int, startTime: Int) = authenticated { user => implicit request =>
    Task.find(id) match {
      case Some(task) => {
        task.move(startTime)
        Ok
      }
      case None => BadRequest
    }
  }
}