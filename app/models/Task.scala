package models
import java.sql.Time

class Task(val id: Long, val name: String, val startTime: Time, val projectId: Long)

class TaskEager(id: Long, name: String, startTime: Time, projectId: Long, val steps: Traversable[StepEager]) extends Task(id, name, startTime, projectId)

trait TaskSteps {
  def steps: List[Step with StepResources]
}

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
  lazy val tasks = new Table[(Long, String, Time, Long)]("tasks") {
      def id = column[Long]("id", O PrimaryKey, O AutoInc)
      def name = column[String]("name", O NotNull)
      def startTime = column[Time]("start_time", O NotNull)
      def projectId = column[Long]("project_id", O NotNull)
      def * = id ~ name ~ startTime ~ projectId
      def noID = name ~ startTime ~ projectId
  }

  def findAll: List[Task] = db withSession { implicit s: Session =>
    (for (task <- tasks) yield (task.*)).list map (t => new Task(t._1, t._2, t._3, t._4))
  }

  def findById(id: Long): Option[Task] = db withSession { implicit s: Session =>
    (for (task <- tasks if task.id === id) yield (task.*)).firstOption map (t => new Task(t._1, t._2, t._3, t._4))
  }
  
  def findWithSteps(id: Long): Option[Task with TaskSteps] = db withSession { implicit s: Session =>
    findById(id) map { task =>
      new Task(task.id, task.name, task.startTime, task.projectId) with TaskSteps {
        override val steps = (for (step <- Step.steps if step.taskId === task.id) yield (step.id)).list map (id => Step.findWithResources(id).get)
      }
    }
  }
  
  def update(id: Long, task: Task): Task = db withSession { implicit s: Session =>
    (for (t <- tasks if t.id === id) yield t.name ~ t.startTime).update((task.name, task.startTime))
    findById(id).get
  }

  def insert(values: (String, Time, Long)): Task = db withSession { implicit s: Session =>
    val id = tasks.noID.insert(values)
    findById(Query(SimpleFunction.nullary[Long]("scope_identity")).first).get
  }

  def delete(id: Long) = db withSession { implicit s: Session =>
    tasks.where(_.id ===  id).delete
  }

  def evolution = tasks.ddl

  
  implicit val jsonFormat = new Writes[Task] {
    override def writes(task: Task): JsValue = JsObject(List(
      "id" -> JsNumber(task.id),
      "name" -> JsString(task.name),
      "startTime" -> JsNumber(task.startTime.getTime),
      "projectId" -> JsNumber(task.projectId)
    ))
  }
}