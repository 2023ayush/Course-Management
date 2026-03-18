package com.ocms.coursemgmt.service;

import com.ocms.coursemgmt.config.RabbitConfig;
import com.ocms.coursemgmt.dto.EmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public EmailProducer(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmail(EmailMessage emailMessage){
        rabbitTemplate.convertAndSend(
                RabbitConfig.EMAIL_EXCHANGE,
                RabbitConfig.EMAIL_ROUTING_KEY,
                emailMessage
        );
        System.out.println("Email message sent to RabbitMQ: " + emailMessage);
    }

}
