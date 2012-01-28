package models

import collection.mutable

case class Project private (id: Int, var name: String, tasks: mutable.Seq[Task], resources: mutable.Seq[Resource])

object Project extends Registry((e: Project) => e.id) {
  def apply(name: String, tasks: mutable.Seq[Task], resources: mutable.Seq[Resource]) =
    create(new Project(freshId(), name, tasks, resources))
}