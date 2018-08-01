/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

import java.util.ArrayList;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 *
 * @author Boris
 */
public class SendMail {

    //private static String TEST_MAIL = "svancar@tetras.sk";//"steeween@gmail.com";//

  
    public void send(String mailfrom, String mailto, String subj, String msg) throws MessagingException {
        //neuvedny prijemca alebo odosielatel
        mailto="developers@tetras.sk";
        if (mailfrom.isEmpty() || mailto.isEmpty()) {
            return;
        }

        //Prekodovanie spravy
        byte[] a = msg.getBytes();
        String msg2 = new String(a);

        //Prekodovanie predmetu spravy
        byte[] b = subj.getBytes();
        String subj2 = new String(b);

        Message message = new MimeMessage(getSession());

        //Pri testovani sa vsetko odosiela na moj email
        

        message.addRecipient(RecipientType.TO, new InternetAddress(mailto));
        message.addFrom(new InternetAddress[]{new InternetAddress(mailfrom)});
        message.setSubject(subj2);
        message.setHeader("Content-Type", "text/plain; charset=\"iso-8859-2\"");
        message.setText(msg2);
        Transport.send(message);
    }

    /*
     * Metoda na posielanie emailu s prilohou 
     */
    public void send_with_attachment(String mailfrom, String mailto, String subj, String msg, ArrayList<String> filepath, ArrayList<String> filename) throws MessagingException {

        //neuvedny prijemca alebo odosielatel
        mailto="developers@tetras.sk";
        if (mailfrom.isEmpty() || mailto.isEmpty()) {
            return;
        }

        String msg2 = msg;
        String subj2 = subj;
        MimeMessage message = new MimeMessage(getSession());

        //Pri testovani sa vsetko odosiela na moj email
       

        //Nastavenie adresy prijemcu
        message.addRecipient(RecipientType.TO, new InternetAddress(mailto));

        //Nastavenie odosielatela spravy
        message.addFrom(new InternetAddress[]{new InternetAddress(mailfrom)});

        //Nastavenie predmetu a hlavicky mailu
        message.setSubject(subj2);
        message.setHeader("Content-Type", "text/plain; charset=\"iso-8859-2\"");

        BodyPart messageBodyPart = new MimeBodyPart();

        //Vlozenie textu
        messageBodyPart.setText(msg2);
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        //Vlozenie priloh
        for (int i = 0; i < filename.size(); i++) {
            messageBodyPart = new MimeBodyPart();

            DataSource source = new FileDataSource(filepath.get(i));
            messageBodyPart.setDataHandler(new DataHandler(source));
            //messageBodyPart.addHeader("Content-Type", "text/plain; charset=\"UTF-8\"");
            //messageBodyPart.addHeader("Content-Transfer-Encoding", "base64");
            try {
                messageBodyPart.setFileName(MimeUtility.encodeText(filename.get(i)));
                //messageBodyPart.setDisposition(Part.ATTACHMENT);
                multipart.addBodyPart(messageBodyPart);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        //Poslanie kompletneho mailu
        message.setContent(multipart);
        Transport.send(message);
    }

    public void sendHTML(String mailfrom, String mailto, String subj, String msg) throws MessagingException {
        //neuvedny prijemca alebo odosielatel
        mailto="developers@tetras.sk";
        if (mailfrom.isEmpty() || mailto.isEmpty()) {
            return;
        }

        //Prekodovanie spravy
        byte[] a = msg.getBytes();
        String msg2 = new String(a);

        //Prekodovanie predmetu spravy
        byte[] b = subj.getBytes();
        String subj2 = new String(b);

        Message message = new MimeMessage(getSession());

        //Pri testovani sa vsetko odosiela na moj email
        

        message.addRecipient(RecipientType.TO, new InternetAddress(mailto));
        message.addFrom(new InternetAddress[]{new InternetAddress(mailfrom)});
        message.setSubject(subj2);
        message.setHeader("Content-Type", "text/html; charset=\"iso-8859-2\"");
        message.setContent(msg2, "text/html; charset=\"iso-8859-2\"");
        Transport.send(message);
    }

    /**
     * Odoslanie html mailu s prilohou
     */
    public void sendHTMLWithAttachment(String mailfrom, String mailto, String subj, String msg, ArrayList<String> filepath, ArrayList<String> filename) throws MessagingException {
        //neuvedny prijemca alebo odosielatel
        mailto="developers@tetras.sk";
        if (mailfrom.isEmpty() || mailto.isEmpty()) {
            return;
        }

        //Prekodovanie spravy
        byte[] a = msg.getBytes();
        String msg2 = new String(a);

        //Prekodovanie predmetu spravy
        byte[] b = subj.getBytes();
        String subj2 = new String(b);

        MimeMessage message = new MimeMessage(getSession());

        //Pri testovani sa vsetko odosiela na moj email
        

        message.addRecipient(RecipientType.TO, new InternetAddress(mailto));
        message.addFrom(new InternetAddress[]{new InternetAddress(mailfrom)});
        message.setSubject(subj2);
        message.setHeader("Content-Type", "text/html; charset=\"iso-8859-2\"");

        BodyPart messageBodyPart = new MimeBodyPart();
        //Vlozenie textu 
        messageBodyPart.setContent(msg2, "text/html; charset=utf-8");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        //Vlozenie priloh
        for (int i = 0; i < filename.size(); i++) {
            messageBodyPart = new MimeBodyPart();

            DataSource source = new FileDataSource(filepath.get(i));
            messageBodyPart.setDataHandler(new DataHandler(source));

            try {
                messageBodyPart.setFileName(MimeUtility.encodeText(filename.get(i)));
                multipart.addBodyPart(messageBodyPart);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        //Poslanie kompletneho mailu
        message.setContent(multipart);
        Transport.send(message);
    }

  

    private Session getSession() {
        Authenticator authenticator = new Authenticator();
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", "mail.tetras.sk");
        properties.setProperty("mail.smtp.port", "25");
        return Session.getInstance(properties, authenticator);
    }

    private class Authenticator extends javax.mail.Authenticator {

        private PasswordAuthentication authentication;

        public Authenticator() {
            String username = "tetrasoft@tetras.sk";
            String password = "eezSJR3h";
            authentication = new PasswordAuthentication(username, password);
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return authentication;
        }
    }

}
