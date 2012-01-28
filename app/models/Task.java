package models;

import java.sql.Time;
import java.util.List;

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
    public Long id;
    
    public final String name;
    
    public final Time startTime;
    
    @OneToMany(cascade = CascadeType.ALL)
    public final List<Step> steps;
    
    public Task(String name, Time startTime, List<Step> steps) {
        this.name = name;
        this.startTime = startTime;
        this.steps = steps;
    }
    
    public static Model.Finder<Long, Task> find = new Model.Finder<Long, Task>(Long.class, Task.class);
}
