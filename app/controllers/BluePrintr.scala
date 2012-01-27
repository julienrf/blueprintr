package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

import models.Project

object BluePrintr extends Controller with Authenticated {
  
  def index = authenticated { user => request =>
    val projects = Project.findAll
    Ok(views.html.index(user, projects))
  }
  
  def project(id: Long) = authenticated { user => request =>
    Project.findWithTasksAndResources(id) match {
      case Some(project) => Ok(views.html.project(user, project))
      case None => NotFound
    }
  }
  
  def createProjectForm = authenticated { user => request =>
    Ok(views.html.projectForm(user, projectForm))
  }
  
  def createProject = authenticated { user => implicit request =>
    projectForm.bindFromRequest.fold(
          errors => BadRequest(views.html.projectForm(user, errors)),
          name => {
            Project.insert(name)
            Redirect(routes.BluePrintr.index)
          }
        )
  }
  
  val projectForm = Form(mapping("name" -> nonEmptyText)(identity)(Some(_)))

  def front = Action {
      Ok(views.html.front())
  }
}
