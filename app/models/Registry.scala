package models

class Registry[A](extractId: A => Int) {
  
  def find(id: Int): Option[A] = entities.get(id)
  def findAll: Seq[A] = entities.values.toSeq
  def create(a: A): A = {
    entities = entities + (extractId(a) -> a)
    a
  }
  
  def freshId() = ids.incrementAndGet()
  
  private val ids = new java.util.concurrent.atomic.AtomicInteger(0)
  private var entities = collection.immutable.SortedMap[Int, A]()
  
}