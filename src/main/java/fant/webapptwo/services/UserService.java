/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.services;

import fant.webapptwo.entities.User;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author marti
 */

@Stateless
//@ApplicationScoped
public class UserService {
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    
    @Transactional
    public User createUser(String name, String username, String password, String userGroup, String userMail){
        System.out.println(name);
        System.out.println(username);
        System.out.println(password);
        System.out.println(userGroup);
        System.out.println(userMail);
        User user = new User(name, username, password, userGroup, userMail);
        em.persist(user);
        em.flush();
        return user;
    }
    @Transactional
    public User createUserByUser(User userFromPost){
        System.out.print(userFromPost.getName());
        User user = new User(userFromPost.getName(), userFromPost.getUsername(), userFromPost.getPassword(), userFromPost.getUserGroup(), userFromPost.getUserMail());
        em.persist(user);
        em.flush();
        return user;
    }
    
    public List <User> getAllUsers(){
        for (User resultList : em.createNamedQuery("User.all", User.class).getResultList()) {
            
            System.out.println("User ID: " + resultList.getId() + " - Username: " + resultList.getUsername());
        }
        return em.createNamedQuery("User.all", User.class).getResultList();
        
    }
    
    public Optional <User> getUser(String username){
        try{
            Optional <User> user = em.createNamedQuery("User.byUsername", User.class).setParameter("username", username).getResultList().stream().findFirst();
        User us = user.get();
        //System.out.println("User with username: " + username + ", has id: " + us.getId());
        return user;
        }
        catch(Exception e) {
            return null;
        }
    }
    /* THIS ONE IS FOR DELETING A USER BASED ON ID, BUT FOR TESTING PURPOUSES IT IS MUCH BETTER TO DO IT BASED ON USERNAME
    public Optional <User> getUser(int id){
        Optional <User> user = em.createNamedQuery("User.byId", User.class).setParameter("id", id).getResultList().stream().findFirst();
        User us = user.get();
        System.out.println("User at ID: " + id + ", has username: " + us.getUsername());
        return user;
    }
    */
    @Transactional
    public void deleteUser(String username){
        Optional <User> tempUser = getUser(username);
        User user = tempUser.get();
        em.remove(user);
        if(getUser(username) == null){
            System.out.print("User with username: " + username + " Has successfully been deleted");
        }
        
    }
    /*THIS IS FOR DELETING USER WITH ID, FOR TESTING PURPOUSES USERNAME ID MUCH BETTER
    @Transactional
    public void deleteUser(int id){
        Optional <User> tempUser = getUser(id);
        User user = tempUser.get();
        em.remove(user);
        if(getUser(id) == null){
            System.out.print("User with id: " + id + " Has successfully been deleted");
        }
        
    }
    */
    
    public User changePassword(String username, String newPassword){
        Optional <User> tempUser = getUser(username);
        User user = tempUser.get();
        user.setPassword(newPassword);
        
        return user;
    }
    
    public User findUserNew(int id){
        return em.find(User.class, id);
    }
    
    public User changePasswordNew(int id, String newPassword){
        User user = this.findUserNew(id);
        user.setPassword(newPassword);
        
        em.merge(user);
        em.flush();
        return user;
    }
    
    
    
    /*
    public User updatePassword(int uId){
        User userToUpdate = em.find(User.class, uId);
        System.out.println(userToUpdate.getName());
        userToUpdate.setPassword("newPassword");
        em.merge(userToUpdate);
        
        return userToUpdate;
    }
*/

}
