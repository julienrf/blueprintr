package models
import java.sql.Time
import scala.collection.SortedSet

case class Task private (id: Int, name: String, startTime: Time, steps: List[Step]) {
  
  lazy val resources: Seq[Resource] = {
    (for {
      step <- steps
      resource <- step.resources
    } yield resource).distinct.sortBy(_.id)
  }
  
}

object Task extends Registry((e: Task) => e.id) {
  def apply(name: String, startTime: Time, steps: List[Step]) =
    save(new Task(freshId(), name, startTime, steps))
}