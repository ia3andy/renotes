package rest;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import io.smallrye.common.annotation.Blocking;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import model.Note;

import static rest.HxController.HxResponseHeader.TRIGGER;

@Blocking
public class Notes extends HxController {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance notes(List<Note> notes, Note currentNote);
        public static native TemplateInstance notes$noteList(List<Note> notes);
        public static native TemplateInstance notes$noteForm(Note currentNote);
    }

    @Path("/")
    public TemplateInstance notes() {
        final List<Note> notes = Note.listAllSortedByLastUpdated();
        if (isHxRequest()) {
            return Templates.notes$noteList(notes);
        }
        return Templates.notes(notes, null);
    }

    @Path("/new-note")
    public TemplateInstance newNote() {
        // not really used
        Note note = new Note();
        if (isHxRequest()) {
            return Templates.notes$noteForm(note);
        }
        return Templates.notes(Note.listAllSortedByLastUpdated(), note);
    }

    @Path("/note/{id}")
    public TemplateInstance editNote(@PathParam("id") Long id) {
        final Note note = Note.findById(id);
        notFoundIfNull(note);
        if (isHxRequest()) {
            hx(TRIGGER, "refreshNoteList");
            return Templates.notes$noteForm(note);
        }
        return Templates.notes(Note.listAllSortedByLastUpdated(), note);
    }

    @Path("/note/{id}/delete")
    @POST
    public void deleteNote(@PathParam("id") Long id) {
        flashHxRequest();
        Note note = Note.findById(id);
        notFoundIfNull(note);
        note.delete();
        final List<Note> notes = Note.listAllSortedByLastUpdated();
        notes();
    }

    @Path("/note/{id}/save")
    @POST
    public void saveNote(@PathParam("id") Long id, @RestForm @NotBlank String name, @RestForm @NotBlank String content) {
        if(validationFailed()) {
            editNote(id);
        }
        Note note = Note.findById(id);
        notFoundIfNull(note);
        note.name = name;
        note.content = content;
        note.updated = new Date();
        note.persist();
        editNote(id);
    }

    @POST
    public void saveNewNote(@RestForm @NotBlank String name, @RestForm String content) {
        if(validationFailed()) {
            newNote();
        }
        Note note = new Note();
        note.name = name;
        note.content = content;
        note.persist();
        editNote(note.id);
    }
}