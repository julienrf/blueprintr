import models._
import play.api._

object Global extends GlobalSettings {
    
    override def beforeStart(application: Application) {
        
        import play.api.db.evolutions.Evolutions._
        
        val ddl = Task.evolution ++ Project.evolution ++ Step.evolution ++ Resource.evolution ++ StepResource.evolution
        
        updateEvolutionScript(
            ups = ddl.createStatements.mkString(";\n"),
            downs = ddl.dropStatements.mkString(";\n")
        )(application)
        
    }
    
    override def onStart(application: Application) {
      
      import java.sql.Time
      import java.awt.Color
      
      if (Project.findAll.isEmpty) {
        val zDay = Project.insert("Zen Day")
        
        val mda = Resource.insert(("Maxime", 1, Color.blue.getRGB, zDay.id))
        val jrf = Resource.insert(("Julien", 1, Color.red.getRGB, zDay.id))
        val poele = Resource.insert(("Poêle", 1, Color.yellow.getRGB, zDay.id))
        
        val crepes = Task.insert(("Faire des crêpes", new Time(8 * 60 * 60 * 1000), zDay.id))
        val pate = Step.insert(("Pâte", 20 * 60, crepes.id))
        StepResource.insert((pate.id, mda.id))
        val repos = Step.insert(("Repos", 30 * 60, crepes.id))
        val cuisson = Step.insert(("Cuisson", 25 * 60, crepes.id))
        StepResource.insert((cuisson.id, mda.id))
        StepResource.insert((cuisson.id, poele.id))
        
        val omelette = Task.insert(("Omelette forestière", new Time(9 * 60 * 60 * 1000), zDay.id))
        println("omelette inserted (%s)" format omelette.id)
        val beating = Step.insert(("Battre les œufs", 10 * 60, omelette.id))
        StepResource.insert((beating.id, jrf.id))
        val cooking = Step.insert(("Cuisson", 10 * 60, omelette.id))
        StepResource.insert((cooking.id, jrf.id))
        StepResource.insert((cooking.id, poele.id))
      }
    }
}