package models

case class Resource private (id: Int, name: String, amount: Int, color: Int)

object Resource extends Registry((e: Resource) => e.id) {
  def apply(name: String, amount: Int, color: Int) =
    save(new Resource(freshId(), name, amount, color))
}
