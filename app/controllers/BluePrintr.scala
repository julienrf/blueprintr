package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.{Project, Resource, Task}
import com.avaje.ebean.Ebean
import collection.JavaConverters._

object BluePrintr extends Controller with Authenticated {
  
  def index = authenticated { user => request =>
    val projects = Project.find.all.asScala.toList
    Ok(views.html.index(user, projects))
  }
  
  def project(id: Int) = authenticated { user => request =>
    Option(Project.find.byId(id)) match {
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
            Ebean.save(new Project(name, List[Task]().asJava, List[Resource]().asJava))
            Redirect(routes.BluePrintr.index)
          }
        )
  }
  
  val projectForm = Form(mapping("name" -> nonEmptyText)(identity)(Some(_)))

  def front = Action {
      Ok(views.html.front())
  }
}
