package controllers

import play.api.mvc.{Controller, Results}
import Results._
import models.Task
import java.sql.Time

object Tasks extends Controller with Authenticated {
  
  def move(id: Int, delta: Int) = authenticated { user => implicit request =>
    Task.find(id) match {
      case Some(task) => {
        val updated = task.copy(startTime = new Time(task.startTime.getTime + delta))
        Task.save(updated)
        Ok
      }
      case None => BadRequest
    }
  }
}