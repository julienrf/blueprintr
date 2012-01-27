package models
import java.sql.Time

case class Task(id: Long, name: String, startTime: Time, projectId: Long)

// DAO layer
object Task {

  import org.scalaquery.session.Session
  import org.scalaquery.ql._
  import org.scalaquery.ql.TypeMapper._
  import org.scalaquery.ql.extended.H2Driver.Implicit._
  import org.scalaquery.ql.extended.{ExtendedTable => Table}
  import java.sql.Date
  import org.scalaquery.session.Database
  import play.api.db.DB
  import play.api.Play.current
  import play.api.libs.json.Writes
  import play.api.libs.json.{JsValue, JsObject, JsNumber, JsString}

  private lazy val db = Database.forDataSource(DB.getDataSource())
  private lazy val Tasks = new Table[(Long, String, Time, Long)]("tasks") {
      def id = column[Long]("id", O PrimaryKey, O AutoInc)
      def name = column[String]("name", O NotNull)
      def startTime = column[Time]("start_time", O NotNull)
      def projectId = column[Long]("project_id", O NotNull)
      def * = id ~ name ~ startTime ~ projectId
      def noID = name ~ startTime ~ projectId
  }

  def findAll: List[Task] = db withSession { implicit s: Session =>
    (for (task <- Tasks) yield (task.*)).list map (t => Task(t._1, t._2, t._3, t._4))
  }

  def findById(id: Long): Option[Task] = db withSession { implicit s: Session =>
    (for (task <- Tasks if task.id === id) yield (task.*)).firstOption map (t => Task(t._1, t._2, t._3, t._4))
  }

  def update(id: Long, task: Task): Task = db withSession { implicit s: Session =>
    (for (t <- Tasks if t.id === id) yield t.name ~ t.startTime).update((task.name, task.startTime))
    findById(id).get
  }

  def insert(values: (String, Time, Long)): Task = db withSession { implicit s: Session =>
    val id = Tasks.noID.insert(values)
    findById(Query(SimpleFunction.nullary[Long]("scope_identity")).first).get
  }

  def delete(id: Long) = db withSession { implicit s: Session =>
    Tasks.where(_.id ===  id).delete
  }

  def evolution = Tasks.ddl

  
  implicit val jsonFormat = new Writes[Task] {
    override def writes(task: Task): JsValue = JsObject(List(
      "id" -> JsNumber(task.id),
      "name" -> JsString(task.name),
      "startTime" -> JsNumber(task.startTime.getTime),
      "projectId" -> JsNumber(task.projectId)
    ))
  }
}