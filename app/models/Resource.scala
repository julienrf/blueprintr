package models
import java.awt.Color

case class Resource(id: Long, name: String, size: Int, color: Color, projectId: Long)

object Resource {
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
  private lazy val resources = new Table[(Long, String, Int, Int, Long)]("resources") {
      def id = column[Long]("id", O PrimaryKey, O AutoInc)
      def name = column[String]("name", O NotNull)
      def size = column[Int]("size", O NotNull)
      def color = column[Int]("color", O NotNull)
      def projectId = column[Long]("project_id", O NotNull)
      def * = id ~ name ~ size ~ color ~ projectId
      def noID = name ~ size ~ color ~ projectId
  }

  def findAll: List[Resource] = db withSession { implicit s: Session =>
    (for (row <- resources) yield (row.*)).list map (t => Resource(t._1, t._2, t._3, new Color(t._4), t._5))
  }

  def findById(id: Long): Option[Resource] = db withSession { implicit s: Session =>
    (for (row <- resources if row.id === id) yield (row.*)).firstOption map (t => Resource(t._1, t._2, t._3, new Color(t._4), t._5))
  }

  def update(id: Long, resource: Resource): Resource = db withSession { implicit s: Session =>
    (for (row <- resources if row.id === id) yield row.noID).update((resource.name, resource.size, resource.color.getRGB, resource.projectId))
    findById(id).get
  }

  def insert(values: (String, Int, Int, Long)): Resource = db withSession { implicit s: Session =>
    val id = resources.noID.insert(values)
    findById(Query(SimpleFunction.nullary[Long]("scope_identity")).first).get
  }

  def delete(id: Long) = db withSession { implicit s: Session =>
    resources.where(_.id ===  id).delete
  }

  def evolution = resources.ddl

  implicit val jsonWrite = new Writes[Resource] {
    override def writes(resource: Resource): JsValue = JsObject(List(
      "id" -> JsNumber(resource.id),
      "name" -> JsString(resource.name),
      "size" -> JsNumber(resource.size),
      "color" -> colorFormat.writes(resource.color),
      "projectId" -> JsNumber(resource.projectId)
    ))
  }
  
  val colorFormat: Writes[Color] = new Writes[Color] {
    override def writes(color: Color): JsValue = JsObject(List(
        "r" -> JsString(color.getRed.toHexString),
        "g" -> JsString(color.getGreen.toHexString),
        "b" -> JsString(color.getBlue.toHexString)
    ))
    /*override def reads(json: JsValue): Color = new Color(
        Integer.parseInt((json \ "r").as[String], 16),
        Integer.parseInt((json \ "g").as[String], 16),
        Integer.parseInt((json \ "b").as[String], 16)
    )*/
  }
}