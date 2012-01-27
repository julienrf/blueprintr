package models

case class Project(id: Long, name: String)

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
    (for (project <- projects) yield (project.*)).list map (t => Project(t._1, t._2))
  }

  def findById(id: Long): Option[Project] = db withSession { implicit s: Session =>
    (for (project <- projects if project.id === id) yield (project.*)).firstOption map (t => Project(t._1, t._2))
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
    
    override def writes(task: Project): JsValue = JsObject(List(
      "id" -> JsNumber(task.id),
      "name" -> JsString(task.name)
    ))

    override def reads(json: JsValue): Project = Project(
      (json \ "id").as[Long],
      (json \ "name").as[String]
    )
  }
}