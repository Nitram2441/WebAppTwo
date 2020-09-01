package fant.webapptwo;

import fant.webapptwo.services.UserService;
import fant.webapptwo.entities.User;
import fant.webapptwo.services.ListingService;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/*
I'll use this class to test various functions, cause i can do it without JAX rs and the painful process
of having to troubleshoot both that and JPA etc while working on new stuff. 
JUST REMEMBER TO COMMENT OUT THINGS THAT CHANGES DB RIGHT AFTER A SUCCESSFUL DEPLOY!!!!!!!!!!!!!!!!!!!!!!!
*/
@ApplicationScoped
public class DataInitializer {
    
    @Inject
    UserService us;
    
    @Inject
    ListingService ls;
    
    @PersistenceContext
    EntityManager em;
    
    public void execute(@Observes @Initialized(ApplicationScoped.class) Object event){
    
        

        //if(dataService.getAllUsers().isEmpty()){
        
            if(us.getUser("deleteMe") == null){
                if(us.createUser("DeleteMe", "deleteMe", "password", "user", "mail") != null){
                    System.out.println("Created user to test delete");
                }
                //System.out.println("Created user to test delete");
            }
        
            //User tom = dataService.createUser("Tom Matthews", "tmatthews", "tmatthews", "user");
            System.out.print("");
            System.out.println("All users in list:");
            us.getAllUsers();
            
            System.out.print("");
            
            System.out.println("User with username daggy112:");
            if(us.getUser("daggy112") != null){
                Optional <User> userGetTemp = us.getUser("daggy112");
            User userGet = userGetTemp.get();
            System.out.println("User with username: " + userGet.getUsername() + ", has id: " + userGet.getId());
            }
            else{
                System.out.println("No such user...");
            }
            
            System.out.print("");
            
            System.out.print("Trying to delete user with id 15!");
            if(us.getUser("deleteMe") != null){
                us.deleteUser("deleteMe");
            }
            else{
                System.out.println("No such user...");
            }
            
            System.out.print("");
            
            User user = us.findUserNew(14);
            //ls.createListing("kkona2", "KOKOKOKOK2OKOKO", "JSJ2SJSJSJSJS", user); //uncomment to add listings
            System.out.print(ls.findListing(14).getTitle());
            ls.deleteListing(13);
            
            
            
            
            /*
            System.out.println("Trying to change password");
            if(us.getUser("daggy112") != null){
                Optional <User> userGetTemp = us.getUser("daggy112");
                User userGet = userGetTemp.get();
                if (userGet.getPassword() == "pass1"){
                    us.changePassword("daggy112", "pass2");
                }
                else{
                    us.changePassword("daggy112", "pass1");
                }
            
            }
            */
        
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
