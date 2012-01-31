package models
import scala.collection.SortedSet
import collection.mutable
import registry.Registry

case class Task private (id: Int, var name: String, var startTime: Int, steps: mutable.Seq[Step]) {
  
  def move(startTime: Int) {
    this.startTime = startTime
  }
  
  def resources: Seq[Resource] = {
    (for {
      step <- steps
      resource <- step.resources
    } yield resource).distinct.sortBy(_.id)
  }
  
  def stepsWithStartTime: Seq[(Step, Int)] = {
    val startTimes = (steps.take(steps.size - 1).map { _.duration }).foldRight(List(startTime)) { (duration, startTimes) =>
      (duration + startTimes.head) +: startTimes
    }
    steps zip startTimes.reverse
  }
  
}

object Task extends Registry((e: Task) => e.id) {
  def apply(name: String, startTime: Int, steps: mutable.Seq[Step]) =
    create(new Task(freshId(), name, startTime, steps))
}