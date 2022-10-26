package util;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import model.Note;

@ApplicationScoped
public class Startup {

    @Transactional
    public void start(@Observes StartupEvent evt) {

        if(LaunchMode.current() == LaunchMode.DEVELOPMENT) {
            Note a = new Note();
            a.name = "my-meeting-with-stephane";
            a.content = """
                    # My meeting with Stephane
                    
                    Talking about *HTMX* with *Quarkus* is really cool! 
                    """;
            a.persist();

            Note b = new Note();
            b.name = "todo";
            b.content = """
                    # My ToDos
                    
                    - [ ] Write an abstract for the KCD France 2023
                    - [x] Create an issue about the weird Renarde redirect :) 
                    """;
            b.persist();
        }
    }
}
