import models._
import play.api._

object Global extends GlobalSettings {
    
    override def onStart(application: Application) {
      
      if (Project.findAll.isEmpty) {
        
        import java.awt.Color
        import play.libs.Time
        import collection.mutable
        
        val mda = Resource("Maxime", 1, Color.blue.getRGB)
        val jrf = Resource("Julien", 1, Color.red.getRGB)
        val poele = Resource("Poêle", 1, Color.yellow.getRGB)
        
        val crepes = Task("Faire des crêpes", Time.parseDuration("30min"), mutable.Seq(
            Step("Pâte", Time.parseDuration("20min"), mutable.Seq(mda)),
            Step("Repos", Time.parseDuration("30min"), mutable.Seq.empty),
            Step("Cuisson", Time.parseDuration("25min"), mutable.Seq(mda, poele))
          ))
        
        val omelette = Task("Omelette forestière", Time.parseDuration("1h"), mutable.Seq(
            Step("Battre les œufs", Time.parseDuration("10min"), mutable.Seq(jrf)),
            Step("Cuisson", Time.parseDuration("15min"), mutable.Seq(jrf, poele))
          ))
        
        val zDay = Project("Zen Day", mutable.Seq(crepes, omelette), mutable.Seq(mda, jrf, poele))
      }
    }
}