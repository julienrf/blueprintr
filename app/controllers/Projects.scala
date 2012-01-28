package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Project, Resource, Task}

object Projects extends Controller with Authenticated {
  
  def index = authenticated { user => request =>
    val projects = Project.findAll
    Ok(views.html.projects.list(user, projects))
  }
  
  def project(id: Int) = authenticated { user => request =>
    Project.find(id) match {
      case Some(project) => Ok(views.html.projects.show(user, project))
      case None => NotFound
    }
  }
  
  def createProjectForm = authenticated { user => request =>
    Ok(views.html.projects.edit(user, projectForm))
  }
  
  def createProject = authenticated { user => implicit request =>
    projectForm.bindFromRequest.fold(
          errors => BadRequest(views.html.projects.edit(user, errors)),
          name => {
            Project(name, Nil, Nil)
            Redirect(routes.Projects.index)
          }
        )
  }
  
  val projectForm = Form(mapping("name" -> nonEmptyText)(identity)(Some(_)))
}
