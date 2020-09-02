/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * kilder:
 * dzone.com/refcardz/getting-started-with-jpa?chapter=2
 * SecureWebApplication prosjekt
 * 
 * TODO: legge til foreign keys i db og her så entities kan referere til hverandre
 */

@Entity
@Table(name="listings")
@NamedQueries({
    @NamedQuery(name = "Listing.all" ,query = "select li from Listing li"),
    @NamedQuery(name = "Listing.byUser", query = "select li from Listing li where li.user.id = :userId"),
    @NamedQuery(name = "Listing.byId", query = "select li from Listing li where li.id = :id")
})
public class Listing implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;
    
    @Column(name="title")
    String title;
    @Column(name="description")
    String description;
    @Column(name="media_source")
    String mediaSource; //is gonna be the path to the picture(S)
    
    //@Column(name="buyer_id")
    @ManyToOne
    @JoinColumn(name="buyer_id", referencedColumnName = "id")
    User buyer;        
    //int buyerId; //gonna be id of person who buys it hopefully i can update it from null to an id when sold
    //@Column(name="seller_id")
    //int sellerId;
    
    //@Column(name="seller_id")
    //int sellerId;
    
    
    @ManyToOne
    @JoinColumn(name="seller_id", referencedColumnName = "id")
    User user;

    public Listing(){
        
    }

    public Listing(String title, String description, String mediaSource, User user) {
        this.title = title;
        this.description = description;
        this.mediaSource = mediaSource;
        this.user = user;
        
    }
    
    
    
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaSource() {
        return mediaSource;
    }

    public int getBuyerId() {
        return buyer.getId();
    }

    public int getSellerId() {
        return user.getId();
    }
    

    public User getUser() {
        return user;
    }
    
    
    public void setBuyer(User buyer){
        this.buyer = buyer;
    }

    
    
}
