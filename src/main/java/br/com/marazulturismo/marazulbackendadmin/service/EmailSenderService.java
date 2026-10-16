package br.com.marazulturismo.marazulbackendadmin.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.smtp.from:${spring.mail.username}}")
    private String fromEmail;


    public void sendEmail(String recipient, String subject, String message){
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(fromEmail);
        email.setTo(recipient);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }


    @Async
    public void sendEmailTemplate(String recipient, String subject, String template, Context context){
        String templateString = templateEngine.process(template, context);
        System.out.println("=== ENVIANDO EMAIL PARA: " + recipient + " | LINK: " + context.getVariable("link") + " ===");
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(recipient);
            messageHelper.setSubject(subject);
            messageHelper.setText(templateString, true);
            mailSender.send(message);
            System.out.println("=== EMAIL ENVIADO COM SUCESSO PARA: " + recipient + " ===");
        }catch (Exception e){
            System.err.println("=== ERRO AO ENVIAR EMAIL PARA " + recipient + ": " + e.getMessage() + " ===");
            e.printStackTrace();
        }
    }
    
}
