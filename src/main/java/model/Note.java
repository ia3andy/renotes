package model;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.util.StringUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Date;
import java.util.List;

@Entity
public class Note extends PanacheEntity {
    public String name;

    @Column(columnDefinition="text")
    public String content;
    public Date created = new Date();
    public Date updated = new Date();

    public Note() {
        super();
    }

    public Note(String name, String content) {
        this();
        this.name = name;
        this.content = content;
    }

    public static List<Note> listAllSortedByLastUpdated() {
        return Note.listAll(Sort.by("updated").descending());
    }

    public static List<Note> search(String search) {
        if(StringUtil.isNullOrEmpty(search)) {
            return listAllSortedByLastUpdated();
        }
        return Note.find("LOWER(name) like LOWER(:search) OR LOWER(content) like LOWER(:search)", Sort.by("updated").descending(), Parameters.with("search", '%' + search + '%')).list();
    }
}