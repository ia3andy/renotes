package rest;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.common.annotation.Blocking;
import model.Note;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.Date;
import java.util.List;

@Blocking
@Path("/notes")
public class Notes extends HxController {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance notes(List<Note> notes, Long currentNoteId, Note currentNote, String search);
        public static native TemplateInstance notes$noteList(List<Note> notes, Long currentNoteId);
        public static native TemplateInstance notes$noteForm(Note currentNote);
    }

    @Path("")
    public TemplateInstance notes(@RestQuery("id") Long id, @RestQuery("search") String search) {
        final List<Note> notes = Note.search(search);
        if (isHxRequest()) {
            return Templates.notes$noteList(notes, id);
        }
        Note note = id != null ? Note.findById(id) : null;
        return Templates.notes(notes, id, note, search);
    }

    public TemplateInstance newNote() {
        // not really used
        Note note = new Note();
        if (isHxRequest()) {
            this.hx(HxResponseHeader.TRIGGER, "refreshNoteList");
            return  concatTemplates(
                    Templates.notes$noteList(Note.listAllSortedByLastUpdated(), null),
                    Templates.notes$noteForm(note)
            );
        }
        return Templates.notes(Note.listAllSortedByLastUpdated(), null, note, null);
    }

    public TemplateInstance editNote(@RestPath("id") Long id) {
        final Note note = Note.findById(id);
        if(note == null) {
            notes(null, null);
            return null;
        }
        if (isHxRequest()) {
            this.hx(HxResponseHeader.TRIGGER, "refreshNoteList");
            return  concatTemplates(
                    Templates.notes$noteList(Note.listAllSortedByLastUpdated(), id),
                    Templates.notes$noteForm(note)
                    );
        }
        return Templates.notes(Note.listAllSortedByLastUpdated(), id, note, null);
    }

    @DELETE
    public String deleteNote(@RestPath("id") Long id) {
        onlyHxRequest();
        Note note = Note.findById(id);
        notFoundIfNull(note);
        note.delete();
        // HTMX is not a fan of 204 No Content for swapping https://github.com/bigskysoftware/htmx/issues/1130
        return "";
    }


    @POST
    public void saveNote(@RestPath("id") Long id, @RestForm @NotBlank String name, @RestForm String content) {
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