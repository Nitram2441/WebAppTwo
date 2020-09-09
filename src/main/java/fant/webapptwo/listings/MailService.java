/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fant.webapptwo.listings;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author marti
 */
@Stateless
public class MailService {
    @Inject
    @ConfigProperty(name = "mail.smtp.host")
    String smtpHost;
    
    @Inject
    @ConfigProperty(name = "mail.smtp.username")
    String smtpUsername;
    
    @Inject
    @ConfigProperty(name = "mail.smtp.password")
    String smtpPassword;
    
    public void sendEmail(String to, String subject, String body){
        try{
            Properties props = new Properties();
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", 587);
            Session mailSession = Session.getInstance(props, new Authenticator(){
                protected PasswordAuthentication getPasswordAuthentication(){
                    return new PasswordAuthentication(smtpUsername, smtpPassword);
                }
            });
            
            Message message = new MimeMessage(mailSession);
            message.setSubject(subject);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setFrom(new InternetAddress(smtpUsername));
            message.setText(body);
            
            Transport.send(message);
        }
        catch(MessagingException ex){
            Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
