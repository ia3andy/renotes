package rest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import io.quarkus.qute.RawString;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import model.Note;

@Blocking
public class Notes extends HxController {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance notes(List<Note> notes, Long currentNoteId, Note currentNote);
        public static native TemplateInstance notes$noteList(List<Note> notes, Long currentNoteId);
        public static native TemplateInstance notes$noteForm(Note currentNote);
    }

    @Path("/")
    public TemplateInstance notes(@QueryParam("id") Long id) {
        final List<Note> notes = Note.listAllSortedByLastUpdated();
        if (isHxRequest()) {
            return Templates.notes$noteList(notes, id);
        }
        Note note = id != null ? Note.findById(id) : null;
        return Templates.notes(notes, id, note);
    }

    @Path("/new-note")
    public TemplateInstance newNote() {
        // not really used
        Note note = new Note();
        if (isHxRequest()) {
            return Templates.notes$noteForm(note);
        }
        return Templates.notes(Note.listAllSortedByLastUpdated(), null, note);
    }

    @Path("/note/{id}")
    public TemplateInstance editNote(@PathParam("id") Long id) {
        final Note note = Note.findById(id);
        if(note == null) {
            notes(null);
            return null;
        }
        if (isHxRequest()) {
            return  concatTemplates(
                    Templates.notes$noteList(Note.listAllSortedByLastUpdated(), id),
                    Templates.notes$noteForm(note)
                    );
        }
        return Templates.notes(Note.listAllSortedByLastUpdated(), id, note);
    }

    @Path("/note/{id}/delete")
    @DELETE
    public String deleteNote(@PathParam("id") Long id) {
        onlyHxRequest();
        Note note = Note.findById(id);
        notFoundIfNull(note);
        note.delete();
        // HTMX is not a fan of 204 No Content for swapping https://github.com/bigskysoftware/htmx/issues/1130
        return "";
    }

    @Path("/note/{id}/save")
    @POST
    public void saveNote(@PathParam("id") Long id, @RestForm @NotBlank String name, @RestForm String content) {
        if(validationFailed()) {
            editNote(id);
            return;
        }
        Note note = Note.findById(id);
        notFoundIfNull(note);
        note.name = name;
        note.content = content;
        note.updated = new Date();
        note.persist();
        flashHxRequest();
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
        flashHxRequest();
        editNote(note.id);
    }
}