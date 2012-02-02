package models

object json {
  
  import play.api.libs.json._
  
  val projectJson = new Writes[Project] {
    override def writes(project: Project): JsValue = JsObject(List(
      "id" -> JsNumber(project.id),
      "name" -> JsString(project.name),
      "tasks" -> JsArray((project.tasks map taskJson.writes).toList)
    ))
  }
  
  val conflictJson = new Writes[Conflict] {
    override def writes(conflict: Conflict): JsValue = JsObject(List(
        "color" -> JsString("#" + conflict.resource.color.toHexString.substring(2)),
        "from" -> JsNumber(conflict.from / 10),
        "duration" -> JsNumber((conflict.to - conflict.from) / 10)
    ))
  }
  
  
  val resourceJson = new Writes[Resource] {
    override def writes(resource: Resource): JsValue = JsObject(List(
      "id" -> JsNumber(resource.id),
      "name" -> JsString(resource.name),
      "size" -> JsNumber(resource.amount),
      "color" -> JsNumber(resource.color)
    ))
  }
  
  
  val taskJson = new Writes[Task] {
    override def writes(task: Task): JsValue = JsObject(List(
      "id" -> JsNumber(task.id),
      "name" -> JsString(task.name),
      "startTime" -> JsNumber(task.startTime)
    ))
  }
  
  
  val stepJson = new Writes[Step] {
    override def writes(step: Step): JsValue = JsObject(List(
      "id" -> JsNumber(step.id),
      "name" -> JsString(step.name),
      "duration" -> JsNumber(step.duration)
      // FIXME resources?
    ))
  }
}