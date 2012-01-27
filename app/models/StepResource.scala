package models

case class StepResource(stepId: Long, resourceId: Long)

// DAO layer
object StepResource {

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
  lazy val stepResources = new Table[(Long, Long)]("step_resource") {
      def stepId = column[Long]("step_id", O NotNull)
      def resourceId = column[Long]("resource_id", O NotNull)
      def * = stepId ~ resourceId
      primaryKey("step_resource_pk", stepId ~ resourceId)
  }

  def insert(values: (Long, Long)) {
    db withSession { implicit s: Session =>
      val id = (stepResources.*).insert(values)
    }
  }

  def delete(stepId: Long, resourceId: Long) = db withSession { implicit s: Session =>
    stepResources.where(row => row.stepId ===  stepId && row.resourceId === resourceId).delete
  }

  def evolution = stepResources.ddl
}