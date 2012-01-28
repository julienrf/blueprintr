package models;

import java.sql.Time;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;


@Entity
public class Step {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;
    
    public final String name;
    
    public final Time duration;
    
    @ManyToMany(cascade = CascadeType.ALL)
    public List<Resource> resources;
    
    public Step(String name, Time duration, List<Resource> resources) {
        this.name = name;
        this.duration = duration;
        this.resources = resources;
        System.out.println("Constructed with resources: " + resources);
    }
    
    public static Model.Finder<Integer, Step> find = new Model.Finder<Integer, Step>(Integer.class, Step.class);
}
