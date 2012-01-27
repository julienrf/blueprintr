package controllers

import play.api._
import play.api.mvc._

import models.Project

object BluePrintr extends Controller with Authenticated {
  
  def index = authenticated { user => request =>
    val projects = Project.findAll
    Ok(views.html.index(user, projects))
  }
  
}