import models._
import play.api._

object Global extends GlobalSettings {
    
    override def onStart(application: Application) {
      
      if (Project.findAll.isEmpty) {
        
        import java.awt.Color
        import play.libs.Time
        import java.sql.{Time => SqlTime}
        
        val mda = Resource("Maxime", 1, Color.blue.getRGB)
        val jrf = Resource("Julien", 1, Color.red.getRGB)
        val poele = Resource("Poêle", 1, Color.yellow.getRGB)
        
        val crepes = Task("Faire des crêpes", new SqlTime(Time.parseDuration("30min")), List(
            Step("Pâte", new SqlTime(Time.parseDuration("20min")), List(mda)),
            Step("Repos", new SqlTime(Time.parseDuration("30min")), Nil),
            Step("Cuisson", new SqlTime(Time.parseDuration("25min")), List(mda, poele))
          ))
        
        val omelette = Task("Omelette forestière", new SqlTime(Time.parseDuration("1h")), List(
            Step("Battre les œufs", new SqlTime(Time.parseDuration("10min")), List(jrf)),
            Step("Cuisson", new SqlTime(Time.parseDuration("15min")), List(jrf, poele))
          ))
        
        val zDay = Project("Zen Day", List(crepes, omelette), List(mda, jrf, poele))
      }
    }
}