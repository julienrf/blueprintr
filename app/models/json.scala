package models

object json {
  
  import play.api.libs.json.{Writes, JsObject, JsValue, JsNumber, JsString}
  
  val projectJson = new Writes[Project] {
    override def writes(project: Project): JsValue = JsObject(List(
      "id" -> JsNumber(project.id),
      "name" -> JsString(project.name)
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