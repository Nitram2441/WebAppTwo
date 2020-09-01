package fant.webapptwo;

import fant.webapptwo.User;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;


/*
Used to initialize, filling up data in the database.
At the bottom shows how to add elements at a later date.
Currently it might just be easier to do that directly in mysql.
*/
@ApplicationScoped
public class DataInitializer {
    
    @Inject
    UserService us;
    
    public void execute(@Observes @Initialized(ApplicationScoped.class) Object event){
        //if(dataService.getAllUsers().isEmpty()){
            User sally = us.createUser("Dag Andread", "daggy", "password", "user", "mail");
            //User tom = dataService.createUser("Tom Matthews", "tmatthews", "tmatthews", "user");
            
            
            //dataService.createQuality("Wonderful", sally);
            //dataService.createQuality("Team Player", sally);
            //dataService.createQuality("Good Judgement", sally);
            //dataService.createQuality("Good Leader", sally);
            

            //dataService.createQuality("Dilligent", tom);
            //dataService.createQuality("Responsible", tom);
            //dataService.createQuality("Cares for his teammates", tom);
            
            //currently just be careful to comment out this shit before running again
            /*
            Optional<User> sallyOp = dataService.getUser("saddams");
            User sally = sallyOp.get();
            
            
            if(sally != null){
                dataService.createQuality("Better than me at setting up a server", sally);
            }
*/
        }
    //}
}
