/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.listings;

import fant.webapptwo.auth.Group;
import fant.webapptwo.auth.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import net.coobird.thumbnailator.Thumbnails;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author marti
 */

@Path("listings")
@Stateless
public class ListingService {
    @Context
    SecurityContext sc;
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "listingphotos")
    String photoPath;
    
    @Inject
    MailService ms;
    
    @GET
    @Path("getlistings")
    //@Produces(MediaType.APPLICATION_JSON))
    public List getAllListings(){
        return em.createNamedQuery("Listing.findAllListings", Listing.class).getResultList();
    }
    
    @POST
    @Path("create")
   // @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.USER})
    public Response addListing(@FormParam("title") String title,
            @FormParam("description") String description){
        Listing listing;
        User user = em.find(User.class, sc.getUserPrincipal().getName());
        listing = new Listing(title, description, user);
        em.persist(listing);
        return Response.ok(listing).build();
    }
    
    @PUT
    @Path("purchase")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Group.USER})
    public Response addBuyer(@FormParam("id") int id){
        Listing listing = findListingById(id);
        User buyer = em.find(User.class, sc.getUserPrincipal().getName());
        listing.setBuyer(buyer);
        em.merge(listing);
        //ms.sendEmail(listing.seller.getEmail(), "One of your items has been sold", listing.getTitle());//virker men fjernet midlertidig fordi jeg fjernet mail og passord i config
        return Response.ok(listing).build();
    }
    
    public Listing findListingById(int id){
        Listing listing = em.createNamedQuery("Listing.findById", Listing.class).setParameter("id", id).getResultList().stream().findFirst().get();
        return listing;
    }
    
    @POST
    @Path("send")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed({Group.USER})
    public Response sendPhoto(FormDataMultiPart multiPart) throws IOException{
        List<FormDataBodyPart> images = multiPart.getFields("image");
        User user = em.find(User.class, sc.getUserPrincipal().getName());
        if (images != null){
            System.out.println("Seems like the server noticed a picture...");
            for(FormDataBodyPart part : images){
                InputStream is = part.getEntityAs(InputStream.class);
                ContentDisposition meta = part.getContentDisposition();
                
                String pid = UUID.randomUUID().toString();
                Files.copy(is, Paths.get(getPhotoPath(), pid));
                
                MediaObject photo = new MediaObject(pid, user, meta.getFileName(), meta.getSize(), meta.getType());
                em.persist(photo);
                System.out.println("Tried to persist photo...");
            }
        }
        return Response.ok().build();
    }
    
    @POST
    @Path("testUpload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response testImage(FormDataMultiPart multiPart){
        List<FormDataBodyPart> images = multiPart.getFields("image");
        if(images != null){
            System.out.println("Managed to send picture");
        }
        return Response.ok().build();
    }
    
    @GET
    @Path("image/{name}")
    @Produces("image/jpeg")
    public Response getImage(@PathParam("name") String name,
            @QueryParam("width") int width){
        if(em.find(MediaObject.class, name) != null) {
            StreamingOutput result = (OutputStream os) -> {
                java.nio.file.Path image = Paths.get(getPhotoPath(),name);
                if(width == 0) {
                    Files.copy(image, os);
                    os.flush();
                } else {
                    Thumbnails.of(image.toFile())
                              .size(width, width)
                              .outputFormat("jpeg")
                              .toOutputStream(os);
                }
            };

            // Ask the browser to cache the image for 24 hours
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            cc.setPrivate(true);

            return Response.ok(result).cacheControl(cc).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }  
    
    
    private String getPhotoPath() {
            return photoPath;
    }
}
