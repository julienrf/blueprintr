package models

case class Step(id: Long, name: String, duration: Int, taskId: Long)

object Step {
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
  private lazy val steps = new Table[(Long, String, Int, Long)]("steps") {
      def id = column[Long]("id", O PrimaryKey, O AutoInc)
      def name = column[String]("name", O NotNull)
      def duration = column[Int]("duration", O NotNull)
      def taskId = column[Long]("task_id", O NotNull)
      def * = id ~ name ~ duration ~ taskId
      def noID = name ~ duration ~ taskId
  }

  def findAll: List[Step] = db withSession { implicit s: Session =>
    (for (row <- steps) yield (row.*)).list map (t => Step(t._1, t._2, t._3, t._4))
  }

  def findById(id: Long): Option[Step] = db withSession { implicit s: Session =>
    (for (row <- steps if row.id === id) yield (row.*)).firstOption map (t => Step(t._1, t._2, t._3, t._4))
  }

  def update(id: Long, step: Step): Step = db withSession { implicit s: Session =>
    (for (row <- steps if row.id === id) yield row.noID).update((step.name, step.duration, step.taskId))
    findById(id).get
  }

  def insert(values: (String, Int, Long)): Step = db withSession { implicit s: Session =>
    val id = steps.noID.insert(values)
    findById(Query(SimpleFunction.nullary[Long]("scope_identity")).first).get
  }

  def delete(id: Long) = db withSession { implicit s: Session =>
    steps.where(_.id ===  id).delete
  }

  def evolution = steps.ddl

  implicit val jsonWrite: Writes[Step] = new Writes[Step] {
    override def writes(step: Step): JsValue = JsObject(List(
      "id" -> JsNumber(step.id),
      "name" -> JsString(step.name),
      "duration" -> JsNumber(step.duration)
    ))
  }
}