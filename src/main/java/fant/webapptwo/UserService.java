/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo;

import fant.webapptwo.User;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

/**
 *
 * @author marti
 */

@ApplicationScoped
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
        return em.createNamedQuery("User.all", User.class).getResultList();
    }
    
    public Optional <User> getUser(int id){
        return em.createNamedQuery("User.byId", User.class).setParameter("id", id).getResultList().stream().findFirst();
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
