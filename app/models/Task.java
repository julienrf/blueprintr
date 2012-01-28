package models;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;


@Entity
public class Task {
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;
    
    public final String name;
    
    public final Time startTime;
    
    @OneToMany(cascade = CascadeType.ALL)
    public final List<Step> steps;
    
    public Task(String name, Time startTime, List<Step> steps) {
        this.name = name;
        this.startTime = startTime;
        this.steps = steps;
    }
    
    public static Model.Finder<Integer, Task> find = new Model.Finder<Integer, Task>(Integer.class, Task.class);
    
    public List<Resource> resources() {
        SortedSet<Resource> resources = new TreeSet<Resource>(new Comparator<Resource>() {
            @Override
            public int compare(Resource r1, Resource r2) {
                return r2.id - r1.id;
            }});
        for (Step step : steps) {
            for (Resource resource : step.resources) {
                resources.add(resource);
            }
        }
        return new ArrayList<Resource>(resources);
    }
}
