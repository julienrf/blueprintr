package models

object json {
  
  import play.api.libs.json._
  import java.text.SimpleDateFormat
  import java.util.Date
  
  val projectJson = new Writes[Project] {
    override def writes(project: Project): JsValue = JsObject(List(
      "id" -> JsNumber(project.id),
      "name" -> JsString(project.name),
      "tasks" -> JsArray((project.tasks map taskJson.writes).toList)
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
  
  
  def projectView(project: Project): JsValue = JsObject(List(
      "id" -> JsNumber(project.id),
      "name" -> JsString(project.name),
      "resources" -> JsArray((project.resources map resourceView).toList),
      "tasks" -> JsArray((project.tasks map taskView).toList),
      "conflicts" -> JsArray((project.resourcesConflicts map conflictView).toList)
  ))
  
  def conflictView(conflict: Conflict): JsValue = JsObject(List(
    "color" -> JsString("#" + conflict.resource.color.toHexString.substring(2)),
    "from" -> JsNumber(conflict.from / 10),
    "duration" -> JsNumber((conflict.to - conflict.from) / 10)
  ))
  
  def resourceView(resource: Resource): JsValue = JsObject(List(
    "id" -> JsNumber(resource.id),
    "name" -> JsString(resource.name),
    "color" -> JsString(resource.color.toHexString.substring(2))
  ))
  
  def taskView(task: Task) = JsObject(List(
    "id" -> JsNumber(task.id),
    "name" -> JsString(task.name),
    "cssWidth" -> JsNumber(task.resources.size * 50),
    "cssTop" -> JsNumber(task.startTime / 10),
    "startTime" -> JsString(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ").format(new Date(task.startTime * 1000 + 1328197073125L))),
    "steps" -> JsArray((task.steps map { stepView(_, task) }).toList)
  ))
  
  def stepView(step: Step, task: Task): JsValue = JsObject(List(
    "id" -> JsNumber(step.id),
    "name" -> JsString(step.name),
    "cssHeight" -> JsNumber(step.duration / 10),
    "resources" -> JsArray((step.resources map { resourceView(_, task) }).toList)
  ))
  
  def resourceView(resource: Resource, task: Task): JsValue = JsObject(List(
    "cssLeft" -> JsNumber(task.resources.indexOf(resource) * 50),
    "color" -> JsString(resource.color.toHexString.substring(2))
  ))
}