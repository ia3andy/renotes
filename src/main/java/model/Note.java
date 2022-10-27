package model;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

import javax.persistence.Entity;
import java.util.Date;
import java.util.List;

@Entity
public class Note extends PanacheEntity {
    public String name;
    public String content;
    public Date created = new Date();
    public Date updated = new Date();

    public static List<Note> listAllSortedByLastUpdated() {
        return Note.listAll(Sort.by("updated").descending());
    }
}