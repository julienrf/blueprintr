package controllers

import play.api.mvc.{Controller, Results}
import Results._
import play.api.libs.json.Json
import models.{Task, Project}
import registry.Transaction

object Tasks extends Controller with Authentication with Transaction {
  
  implicit val taskJson = models.json.taskJson
  
  def move(projectId: Int, taskId: Int, startTime: Int) = authenticated { user => implicit request =>
    (for {
      project <- Project.find(projectId)
      task <- Task.find(taskId) if project.tasks.contains(task)
    } yield {
      atomic {
        task.move(startTime)
        ProjectCollaboration.notify(project, Update(user, Json.toJson(task)))
      }
      Ok
    }) getOrElse BadRequest
  }
}