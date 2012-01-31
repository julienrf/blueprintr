package models

import registry.Registry

case class Resource private (id: Int, var name: String, var amount: Int, var color: Int)

object Resource extends Registry((e: Resource) => e.id) {
  def apply(name: String, amount: Int, color: Int) =
    create(new Resource(freshId(), name, amount, color))
}
