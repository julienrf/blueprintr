import models._
import play.api._
import com.avaje.ebean.Ebean

object Global extends GlobalSettings {
    
    override def onStart(application: Application) {
      
      if (Project.find.all.isEmpty) {
        
        import java.awt.Color
        import play.libs.Time
        import java.sql.{Time => SqlTime}
        import collection.JavaConverters._
        
        val mda = new Resource("Maxime", 1, Color.blue.getRGB)
        val jrf = new Resource("Julien", 1, Color.red.getRGB)
        val poele = new Resource("Poêle", 1, Color.yellow.getRGB)
        
        val zDay = new Project("Zen Day", List[Task]().asJava, List(mda, jrf, poele).asJava)
        
        Ebean.save(zDay)
        
        val foundZDay = Ebean.find(classOf[Project]).findUnique
        
        val crepes = new Task("Faire des crêpes", new SqlTime(Time.parseDuration("30min")), List(
            new Step("Pâte", new SqlTime(Time.parseDuration("20min")), List(mda).asJava),
            new Step("Repos", new SqlTime(Time.parseDuration("30min")), List[Resource]().asJava),
            new Step("Cuisson", new SqlTime(Time.parseDuration("25min")), List(mda, poele).asJava)
          ).asJava)
        
        val omelette = new Task("Omelette forestière", new SqlTime(Time.parseDuration("1h")), List(
            new Step("Battre les œufs", new SqlTime(Time.parseDuration("10min")), List(jrf).asJava),
            new Step("Cuisson", new SqlTime(Time.parseDuration("15min")), List(jrf, poele).asJava)
          ).asJava)
        
        foundZDay.tasks.add(crepes)
        foundZDay.tasks.add(omelette)
        
        Ebean.save(foundZDay)
      }
    }
}