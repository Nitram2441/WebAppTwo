/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.services;

import fant.webapptwo.entities.Listing;
import fant.webapptwo.entities.User;
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
public class ListingService {
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    
    @Transactional
    public Listing createListing(String title, String description, String mediaSource, User user){
        Listing newListing = new Listing(title, description, mediaSource, user);
        em.persist(newListing);
        em.flush();
        return newListing;
        
    }
    
    public List<Listing> getAllListings(){
        return em.createNamedQuery("Listing.all", Listing.class).getResultList();
    }
    
    public List<Listing> getListingByUser(User user){
        for (Listing resultList : em.createNamedQuery("Listing.byUser", Listing.class).setParameter("userId", user.getId()).getResultList()) {
            
            System.out.println(resultList.getTitle());
        }
        return em.createNamedQuery("Listing.byUser", Listing.class).setParameter("userId", user.getId()).getResultList();
    }
    
    public Listing findListing(int id){
        Listing listing = em.createNamedQuery("Listing.byId", Listing.class).setParameter("id", id).getResultList().stream().findFirst().get();
        return listing;
    }
    
    @Transactional
    public void deleteListing(int id){
        Listing listing = findListing(id);
        //System.out.println(listing.getTitle());
        em.remove(listing);
    }
}

