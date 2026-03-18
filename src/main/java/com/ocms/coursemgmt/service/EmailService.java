package com.ocms.coursemgmt.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;

    }


    public CompletableFuture<Void> sendEmail(String toEmail, String subject, String body){
        return CompletableFuture.runAsync(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                System.out.println("Email send to " + toEmail);
            } catch (Exception e) {
                System.out.println("Failed to send email to " + toEmail + ":" + e.getMessage());
            }
        });

    }


}
