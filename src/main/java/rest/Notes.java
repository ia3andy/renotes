package rest;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import io.smallrye.common.annotation.Blocking;
import org.jboss.resteasy.reactive.RestForm;

import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import io.quarkiverse.renarde.Controller;
import model.Note;

@Blocking
public class Notes extends Controller {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance notes(List<Note> notes, Note currentNote);
    }

    @Path("/")
    public TemplateInstance notes() {
        return Templates.notes(Note.listAll(), null);
    }

    @Path("/new-note")
    public TemplateInstance newNote() {
        return Templates.notes(Note.listAll(), new Note());
    }

    @Path("/note/{id}")
    public TemplateInstance editNote(@PathParam("id") Long id) {
        return Templates.notes(Note.listAll(), Note.findById(id));
    }

    @Path("/note/{id}")
    @POST
    public void saveNote(@PathParam("id") Long id, @RestForm @NotBlank String name, @RestForm @NotBlank String content) {
        if(validationFailed()) {
            editNote(id);
        }
        Note note = Note.findById(id);
        note.name = name;
        note.content = content;
        note.persist();
        editNote(id);
    }

    @POST
    public void saveNewNote(@RestForm @NotBlank String name, @RestForm @NotBlank String content) {
        if(validationFailed()) {
            notes();
        }
        Note note = new Note();
        note.name = name;
        note.content = content;
        note.persist();
        editNote(note.id);
    }
}