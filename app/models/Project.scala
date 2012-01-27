package models

class Project(val id: Long, val name: String)

trait ProjectTasks {
  def tasks: List[Task with TaskSteps]
}

trait ProjectResources {
  def resources: List[Resource]
}

object Project {
  import org.scalaquery.session.Session
  import org.scalaquery.ql._
  import org.scalaquery.ql.TypeMapper._
  import org.scalaquery.ql.extended.H2Driver.Implicit._
  import org.scalaquery.ql.extended.{ExtendedTable => Table}
  import java.sql.Date
  import org.scalaquery.session.Database
  import play.api.db.DB
  import play.api.Play.current
  import play.api.libs.json.Format
  import play.api.libs.json.{JsValue, JsObject, JsNumber, JsString}

  private lazy val db = Database.forDataSource(DB.getDataSource())
  private lazy val projects = new Table[(Long, String)]("projects") {
      def id = column[Long]("id", O PrimaryKey, O AutoInc)
      def name = column[String]("name", O NotNull)
      def * = id ~ name
      def noID = name
  }

  def findAll: List[Project] = db withSession { implicit s: Session =>
    (for (project <- projects) yield (project.*)).list map (t => new Project(t._1, t._2))
  }

  def findById(id: Long): Option[Project] = db withSession { implicit s: Session =>
    (for (project <- projects if project.id === id) yield (project.*)).firstOption map (t => new Project(t._1, t._2))
  }
  
  def findWithTasksAndResources(id: Long): Option[Project with ProjectTasks with ProjectResources] = db withSession { implicit s: Session =>
    findById(id) map { project =>
      new Project(project.id, project.name) with ProjectTasks with ProjectResources {
        override val tasks = (for (task <- Task.tasks if task.projectId === project.id) yield task.id).list flatMap Task.findWithSteps
        override val resources = (for (resource <- Resource.resources if resource.projectId === project.id) yield (resource.*)).list map (r => new Resource(r._1, r._2, r._3, new java.awt.Color(r._4), r._5))
      }
    }
  }

  def update(id: Long, project: Project): Project = db withSession { implicit s: Session =>
    (for (project <- projects if project.id === id) yield project.noID).update((project.name))
    findById(id).get
  }

  def insert(name: String): Project = db withSession { implicit s: Session =>
    val id = projects.noID.insert(name)
    findById(Query(SimpleFunction.nullary[Long]("scope_identity")).first).get
  }

  def delete(id: Long) = db withSession { implicit s: Session =>
    projects.where(_.id ===  id).delete
  }

  def evolution = projects.ddl

  implicit val jsonFormat: Format[Project] = new Format[Project] {
    
    override def writes(project: Project): JsValue = JsObject(List(
      "id" -> JsNumber(project.id),
      "name" -> JsString(project.name)
    ))

    override def reads(json: JsValue): Project = new Project(
      (json \ "id").as[Long],
      (json \ "name").as[String]
    )
  }
}