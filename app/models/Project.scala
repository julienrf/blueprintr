package models

class Project(val id: Long, val name: String)

class ProjectEager(id: Long, name: String, val tasks: Traversable[TaskEager], val resources: Traversable[Resource]) extends Project(id, name)

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

  def find(id: Long): Option[Project] = db withSession { implicit s: Session =>
    (for (project <- projects if project.id === id) yield (project.*)).firstOption map (t => new Project(t._1, t._2))
  }
  
  def findEager(id: Long): Option[ProjectEager] = db withSession { implicit s: Session =>
    val rows = (for {
      project <- projects if project.id === id
      task <- Task.tasks if task.projectId === project.id
      step <- Step.steps if step.taskId === task.id
      resource <- Resource.resources if resource.projectId === project.id
      stepResource <- StepResource.stepResources if stepResource.stepId === step.id && stepResource.resourceId === resource.id
    } yield (project.id ~ project.name ~ task.id ~ task.name ~ task.startTime ~ step.id ~ step.name ~ step.duration ~ resource.id ~ resource.name ~ resource.size ~ resource.color)).list map {
      case (pId, pName, tId, tName, tStartTime, sId, sName, sDuration, rId, rName, rSize, rColor) => ((pId, pName), (tId, tName, tStartTime), (sId, sName, sDuration), (rId, rName, rSize, rColor))
    }
    val rawResult = rows.groupBy { _._1 }.mapValues { row =>
      (row map { case (project, task, step, resource) => (task, step, resource) }).groupBy { _._1 }.mapValues { row =>
        (row map { case (task, step, resource) => (step, resource) }).groupBy { _._1 }.mapValues { row =>
          (row map { case (step, resource) => resource })
        }
      }
    }
    (for {
      ((pId, name), result) <- rawResult
    } yield new ProjectEager(
        pId,
        name,
        for {
          ((tId, name, startTime), result2) <- result
        } yield new TaskEager(
            tId,
            name,
            startTime,
            tId,
            for {
              ((sId, name, duration), result3) <- result2
            } yield new StepEager(
                sId,
                name,
                duration,
                tId,
                for {
                  (rId, name, size, color) <- result3
                } yield new Resource(rId, name, size, new java.awt.Color(color), pId)
              )
          ),
        (for (resource <- Resource.resources if resource.projectId === id) yield resource.*).list map { r => Resource(r._1, r._2, r._3, new java.awt.Color(r._4), r._5) }
      )).find(_.id == id)
  }

  def update(id: Long, project: Project): Project = db withSession { implicit s: Session =>
    (for (project <- projects if project.id === id) yield project.noID).update((project.name))
    find(id).get
  }

  def insert(name: String): Project = db withSession { implicit s: Session =>
    val id = projects.noID.insert(name)
    find(Query(SimpleFunction.nullary[Long]("scope_identity")).first).get
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