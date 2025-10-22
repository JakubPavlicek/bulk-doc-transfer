package com.asynchronous.app.service;

import com.asynchronous.app.config.RabbitMQProperties;
import com.asynchronous.app.model.FileMessage;
import com.asynchronous.app.model.SubmissionMessage;
import com.shared.core.entity.DocumentSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitProducer {

    private final RabbitMQProperties rabbitMQProperties;
    private final RabbitTemplate rabbitTemplate;

    public void sendSubmissionMessage(DocumentSubmission submission, List<FileMessage> fileMessages) {
        log.info("Sending message with submission: {}", submission.getId());

        SubmissionMessage message = new SubmissionMessage(submission.getId(), fileMessages);

        rabbitTemplate.convertAndSend(
            rabbitMQProperties.getExchangeName(),
            rabbitMQProperties.getRoutingKey(),
            message
        );
    }

}
