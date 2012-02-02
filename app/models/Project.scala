package models

import collection.mutable
import registry.Registry

case class Conflict(resource: Resource, from: Int, to: Int)

case class Project private (id: Int, var name: String, tasks: mutable.Seq[Task], resources: mutable.Seq[Resource]) {
  
  def resourcesConflicts = {
    (for {
      (task, i) <- tasks.take(tasks.size - 1).zipWithIndex
      (step, startTime) <- task.stepsWithStartTime
      otherTask <- tasks.drop(i + 1)
      (otherStep, otherStartTime) <- otherTask.stepsWithStartTime
      if startTime < otherStartTime + otherStep.duration && startTime + step.duration > otherStartTime
      resource <- step.resources
      if otherStep.resources.contains(resource)
    } yield {
      Conflict(
        resource,
        math.max(startTime, otherStartTime),
        math.min(startTime + step.duration, otherStartTime + otherStep.duration)
      )
    }).toList
  }
}

object Project extends Registry((e: Project) => e.id) {
  def apply(name: String, tasks: mutable.Seq[Task], resources: mutable.Seq[Resource]) =
    create(new Project(freshId(), name, tasks, resources))
}