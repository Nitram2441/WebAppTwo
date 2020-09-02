/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.resources;

import fant.webapptwo.entities.User;
import fant.webapptwo.services.UserService;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author marti
 */
@Path("users")
public class UserResource {
    
    @Inject
    UserService us;
    
    @Path("userbyusername/{username}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserByUsername(@PathParam("username") String username){
        return us.getUser(username).get();
    }
}
