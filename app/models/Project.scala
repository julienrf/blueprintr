package models

case class Project private (id: Int, name: String, tasks: Seq[Task], resources: Seq[Resource])

object Project extends Registry((e: Project) => e.id) {
  def apply(name: String, tasks: Seq[Task], resources: Seq[Resource]) =
    save(new Project(freshId(), name, tasks, resources))
}