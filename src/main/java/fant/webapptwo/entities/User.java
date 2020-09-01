/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * kilder:
 * dzone.com/refcardz/getting-started-with-jpa?chapter=2
 * SecureWebApplication prosjekt
 * 
 * TODO: legge til foreign keys i db og her så entities kan referere til hverandre
 */

@Entity
@Table(name="users")
@NamedQueries({
    @NamedQuery(name = "User.all" ,query = "select us from User us"),
    @NamedQuery(name = "User.byId", query = "select us from User us where us.id = :id"),
    @NamedQuery(name = "User.byUsername", query = "select us from User us where us.username = :username")
    //@NamedQuery(name = "User.UpdatePassword", query = "update us.password from User us where us.id = :id")
})
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    int id;
    @Column(name="name")
    String name;
    @Column(name="username")
    String username;
    @Column(name="user_password")
    String password;
    @Column(name="user_group")
    String userGroup;
    @Column(name="user_mail")
    String userMail;

    public User(){
        
    }
    public User(String name, String username, String password, String userGroup, String userMail){
        this.name = name;
        this.username = username;
        this.password = password;
        this.userGroup = userGroup;
        this.userMail = userMail;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
