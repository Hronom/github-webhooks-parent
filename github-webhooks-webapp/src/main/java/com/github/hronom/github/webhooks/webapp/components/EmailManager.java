package com.github.hronom.github.webhooks.webapp.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@Service("EmailManager")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EmailManager {
    private final Logger logger = LogManager.getLogger();

    public String workDirectory;

    private JavaMailSenderImpl mailSender;
    private SimpleMailMessage templateMessage;

    @Autowired
    public EmailManager(ServletContext servletContext) {
        workDirectory = servletContext.getInitParameter("com.github.hronom.githubwebhooks.dir");
        if (workDirectory == null) {
            workDirectory = System.getProperty("user.dir");
        }
        logger.info("Created WebAppManager: " + workDirectory);

        try {
            File dataFile = new File(workDirectory + File.separator + "settings.xml");

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(dataFile);

            Element root = doc.getDocumentElement();

            NodeList account;
            account = root.getElementsByTagName("account");

            if (account.getLength() > 0) {
                Element extractElem = (Element) account.item(0);

                final String username = extractElem.getAttribute("username");
                final String password = extractElem.getAttribute("password");

                // Get system properties
                Properties properties = System.getProperties();
                properties.setProperty("mail.smtp.auth", "true");
                properties.setProperty("mail.smtp.host", "smtp.gmail.com");
                properties.setProperty("mail.smtp.port", "587");
                properties.setProperty("mail.smtp.starttls.enable", "true");

                Session session = Session.getDefaultInstance(
                    properties, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    }
                );

                mailSender = new JavaMailSenderImpl();
                mailSender.setJavaMailProperties(properties);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.error(e.getMessage());
        }

        templateMessage = new SimpleMailMessage();

        logger.info("Created EmailManager");
    }

    public void testSend() {
        // Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        msg.setTo("hronom@gmail.com");
        msg.setSubject("Test subject");
        msg.setText("Test text");
        try {
            mailSender.send(msg);
        } catch (MailException ex) {
            // simply log it and go on...
            logger.error(ex.getMessage());
        }
    }
}