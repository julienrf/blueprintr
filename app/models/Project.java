package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;


@Entity
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    
    public final String name;
    
    @OneToMany(cascade = CascadeType.ALL)
    public final List<Task> tasks;
    
    @OneToMany(cascade = CascadeType.ALL)
    public final List<Resource> resources;
    
    public Project(String name, List<Task> tasks, List<Resource> resources) {
        this.name = name;
        this.tasks = tasks;
        this.resources = resources;
    }
    
    public static Model.Finder<Long, Project> find = new Model.Finder<Long, Project>(Long.class, Project.class);
}
