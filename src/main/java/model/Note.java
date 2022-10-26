package model;


import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;
import java.util.Date;

@Entity
public class Note extends PanacheEntity {
    public String name;
    public String content;
    public Date created = new Date();
    public Date updated = new Date();
}