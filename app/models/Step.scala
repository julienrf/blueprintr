package models

import java.sql.Time

case class Step private (id: Int, name: String, duration: Time, resources: Seq[Resource])

object Step extends Registry((e: Step) => e.id) {
  def apply(name: String, duration: Time, resources: Seq[Resource]) =
    save(new Step(freshId(), name, duration, resources))
}
