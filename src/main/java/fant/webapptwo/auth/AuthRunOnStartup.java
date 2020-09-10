/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.auth;

import fant.webapptwo.listings.Listing;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.PasswordHash;

/**
 *
 * @author marti
 */
@Singleton
@Startup
public class AuthRunOnStartup {
    @PersistenceContext
    EntityManager em;
    
    @Inject
    PasswordHash hasher;

    @PostConstruct
    public void init() {
        long groups = (long) em.createQuery("SELECT count(g.name) from Group g").getSingleResult();
        if(groups == 0) {
            em.persist(new Group(Group.USER));
            em.persist(new Group(Group.ADMIN));
        }
        /*
        User user = new User();
        user.setUserid("admin1");
        user.setPassword(hasher.generate("admin".toCharArray()));
        Group usergroup = em.find(Group.class, Group.USER);
        Group admingroup = em.find(Group.class, Group.ADMIN);
        user.getGroups().add(usergroup);
        user.getGroups().add(admingroup);

        em.merge(user);
        
        */

    }
    
    
}
