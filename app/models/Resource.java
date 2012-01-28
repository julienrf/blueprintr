package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Resource {
    
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;
    
    public final String name;
    
    public final int amount;
    
    public final int color;
    
    public Resource(String name, int amount, int color) {
        this.name = name;
        this.amount = amount;
        this.color = color;
    }

    public static Model.Finder<Integer, Resource> find = new Model.Finder<Integer, Resource>(Integer.class, Resource.class);
}