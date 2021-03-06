/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import lombok.extern.java.Log;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import javax.annotation.Resource;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import fant.webapptwo.resources.DataSourceProducer;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.eclipse.microprofile.config.inject.ConfigProperty;


import org.eclipse.microprofile.jwt.JsonWebToken;


/**
 *
 * @author marti
 */
@Path("auth")
@Stateless
@Log
public class AuthenticationService {

    private static final String INSERT_USERGROUP = "INSERT INTO AUSERGROUP(NAME,USERID) VALUES (?,?)";
    private static final String DELETE_USERGROUP = "DELETE FROM AUSERGROUP WHERE NAME = ? AND USERID = ?";

    @Inject
    KeyService keyService;

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    @ConfigProperty(name = "mp.jwt.verify.issuer", defaultValue = "issuer")
    String issuer;

    @Resource(lookup = DataSourceProducer.JNDI_NAME)
    DataSource dataSource;

    @PersistenceContext
    EntityManager em;

    @Inject
    PasswordHash hasher;

    @Inject
    JsonWebToken principal;

    /**
     *
     * @param uid
     * @param pwd
     * @param request
     * @return
     */
    @GET
    @Path("login")
    public Response login(
            @QueryParam("uid") @NotBlank String uid,
            @QueryParam("pwd") @NotBlank String pwd,
            @Context HttpServletRequest request) {
        CredentialValidationResult result = identityStoreHandler.validate(
                new UsernamePasswordCredential(uid, pwd));

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            String token = issueToken(result.getCallerPrincipal().getName(),
                    result.getCallerGroups(), request);
            
            return Response
                    .ok(token)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    /**
     *
     * @param name
     * @param groups
     * @param request
     * @return
     */
    private String issueToken(String name, Set<String> groups, HttpServletRequest request) {
        try {
            Date now = new Date();
            Date expiration = Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant());
            JwtBuilder jb = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("kid", "abc-1234567890")
                    .setSubject(name)
                    .setId("a-123")
                    //.setIssuer(issuer)
                    .claim("iss", issuer)
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .claim("upn", name)
                    .claim("groups", groups)
                    .claim("aud", "aud")
                    .claim("auth_time", now)
                    .signWith(keyService.getPrivate());
            return jb.compact();
        } catch (InvalidKeyException t) {
            log.log(Level.SEVERE, "Failed to create token", t);
            throw new RuntimeException("Failed to create token", t);
        }
    }

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(
            @Size(min = 6, message = ("Username must be 6 characters or more"))@FormParam("uid") String uid, 
            @Size(min = 8, message = ("Password myst be 8 characters or more"))@FormParam("pwd") String pwd) {
        
        User user = em.find(User.class, uid);
        if (user != null) {
            log.log(Level.INFO, "user already exists {0}", uid);
            
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            user = new User();
            user.setUserid(uid);
            user.setPassword(hasher.generate(pwd.toCharArray()));
            Group usergroup = em.find(Group.class, Group.USER);
            user.getGroups().add(usergroup);
            
            return Response.ok(em.merge(user)).build();
        }
    }
    
    @PUT
    @Path("adddetails")
    @RolesAllowed(value = {Group.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDetails(
            @Email @FormParam("email") String email){
        User user = em.find(User.class, principal.getName());
        user.setEmail(email);
        em.merge(user);
        return Response.ok(user).build();
    }
    //Just for testing purpouses
    @POST
    @Path("createadmin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUserAdmin(@FormParam("uid") String uid, @FormParam("pwd") String pwd) {
        
        User user = em.find(User.class, uid);
        if (user != null) {
            log.log(Level.INFO, "user already exists {0}", uid);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            user = new User();
            user.setUserid(uid);
            user.setPassword(hasher.generate(pwd.toCharArray()));
            Group adminGroup = em.find(Group.class, Group.ADMIN);
            Group userGroup = em.find(Group.class, Group.USER);
            user.getGroups().add(adminGroup);
            user.getGroups().add(userGroup);
            return Response.ok(em.merge(user)).build();
        }
    }
    
    /**
     *
     * @return
     */
    @GET
    @Path("currentuser")    
    @RolesAllowed(value = {Group.USER})
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser() {
        return em.find(User.class, principal.getName());
    }
    
    @GET
    @Path("users")
    @RolesAllowed(value = {Group.ADMIN})
    public List<User> getAllUsers(){
        return em.createNamedQuery(User.FIND_ALL_USERS, User.class).getResultList();
    }

    /**
     *
     * @param uid
     * @param role
     * @return
     */
    @PUT
    @Path("addrole")
    @RolesAllowed(value = {Group.ADMIN})
    public Response addRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
        if (!roleExists(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        User user = em.find(User.class, uid);
        Group group = em.find(Group.class, role);
        if(!user.getGroups().contains(group)){
            user.groups.add(group);
            em.merge(user);
        }
        return Response.ok(user).build();
    }

    /**
     *
     * @param role
     * @return
     */
    private boolean roleExists(String role) {
        boolean result = false;

        if (role != null) {
            switch (role) {
                case Group.ADMIN:
                case Group.USER:
                    result = true;
                    break;
            }
        }

        return result;
    }

    /**
     *
     * @param uid
     * @param role
     * @return
     */
    @PUT
    @Path("removerole")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(value = {Group.ADMIN})
    public Response removeRole(@QueryParam("uid") String uid, @QueryParam("role") String role) {
        if (!roleExists(role)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User user = em.find(User.class, uid);
        Group group = em.find(Group.class, role);
        if(user.getGroups().contains(group)){
            user.groups.remove(group);
            em.merge(user);
        }
        return Response.ok(user).build();
    }

    /**
     *
     * @param uid
     * @param password
     * @param sc
     * @return
     */
    @PUT
    @Path("changepassword")
    @RolesAllowed(value = {Group.USER})
    public Response changePassword(@QueryParam("uid") String uid,
            @Size(min = 8, message = ("New password must be at least 8 characters long."))@QueryParam("pwd") String password,
            @Context SecurityContext sc) {
        String authuser = sc.getUserPrincipal() != null ? sc.getUserPrincipal().getName() : null;
        if (authuser == null || uid == null || (password == null || password.length() < 3)) {
            log.log(Level.SEVERE, "Failed to change password on user {0}", uid);
            return Response.status(Response.Status.BAD_REQUEST).build();
            
        }

        if (authuser.compareToIgnoreCase(uid) != 0 && !sc.isUserInRole(Group.ADMIN)) {
            log.log(Level.SEVERE,
                    "No admin access for {0}. Failed to change password on user {1}",
                    new Object[]{authuser, uid});
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            User user = em.find(User.class, uid);
            user.setPassword(hasher.generate(password.toCharArray()));
            em.merge(user);
            return Response.ok().build();
        }
    }
}
