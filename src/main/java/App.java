import io.quarkiverse.renarde.Controller;
import rest.Notes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;


public class App extends Controller {

    @Path("/")
    public void home() {
        redirect(Notes.class).notes(null, null);
    }
}