/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.listings;

import fant.webapptwo.auth.User;
import java.io.Serializable;
import java.util.List;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author marti
 */
@Entity
@Data @EqualsAndHashCode(exclude = {"seller"}, callSuper = false)
@AllArgsConstructor
@NamedQuery(name = "Listing.findById",
        query = "select l from Listing l where l.id = :id")
@NamedQuery(name = "Listing.findAllListings",
        query = "select l from Listing l")
public class Listing extends AbstractDomain{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;
    
    String title;
    
    String description;
    
    @JsonbTypeAdapter(MediaObjectAdapter.class)
    @OneToMany
    List<MediaObject> photos;
    
    @ManyToOne
    @JoinColumn(name="seller_id", referencedColumnName = "userid")
    User seller;
    
    @ManyToOne
    @JoinColumn(name="buyer_id", referencedColumnName = "userid")
    User buyer;
    
    public Listing(String title, String description, User seller){
        this.title = title;
        this.description = description;
        this.seller = seller;
    }
    
    public Listing(){
        
    }
}
