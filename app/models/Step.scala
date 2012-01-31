package models

import collection.mutable
import registry.Registry

case class Step private (id: Int, var name: String, var duration: Int, resources: mutable.Seq[Resource])

object Step extends Registry((e: Step) => e.id) {
  def apply(name: String, duration: Int, resources: mutable.Seq[Resource]) =
    create(new Step(freshId(), name, duration, resources))
}
