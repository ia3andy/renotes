import io.quarkiverse.renarde.Controller;
import rest.Notes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


public class App extends Controller {

    @Path("/")
    public void home() {
        redirect(Notes.class).notes(null, null);
    }
}