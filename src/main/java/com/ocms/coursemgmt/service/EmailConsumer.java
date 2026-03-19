//package com.ocms.coursemgmt.service;
//
//import com.ocms.coursemgmt.config.RabbitConfig;
//import com.ocms.coursemgmt.dto.EmailMessage;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring6.SpringTemplateEngine;
//
//import java.util.concurrent.CompletableFuture;
//
//@Service
//public class EmailConsumer {
//
//    private final JavaMailSender javaMailSender;
//    private final SpringTemplateEngine templateEngine;
//
//    public EmailConsumer(JavaMailSender javaMailSender,SpringTemplateEngine templateEngine){
//        this.javaMailSender = javaMailSender;
//        this.templateEngine = templateEngine;
//
//    }
//
//    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
//    public void consumeEmail(EmailMessage message){
//        CompletableFuture.runAsync(() -> {
//        try{
//            Context context = new Context();
//            context.setVariable("name", message.getName());
//            context.setVariable("email", message.getTo());
//            context.setVariable("customMessage", message.getBody());
//
//            String htmlBody = templateEngine.process("registration-email",context);
//
//
//            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
//            helper.setTo(message.getTo());
//            helper.setSubject(message.getSubject());
//            helper.setText(message.getBody(), true);
//
//            javaMailSender.send(mimeMessage);
//            System.out.println("Email sent to " + message.getTo());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        });
//    }
//}
