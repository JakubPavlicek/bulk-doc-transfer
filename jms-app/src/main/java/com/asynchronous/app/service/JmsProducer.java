package com.asynchronous.app.service;

import com.asynchronous.app.model.FileMessage;
import com.asynchronous.app.model.SubmissionMessage;
import com.shared.core.entity.DocumentSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class JmsProducer {

    @Value("${spring.jms.jndi-name}")
    private String destination;

    private final JmsTemplate jmsTemplate;

    public void sendSubmissionMessage(DocumentSubmission submission, List<FileMessage> fileMessages) {
        log.info("Sending message with submission: {}", submission.getId());

        SubmissionMessage message = new SubmissionMessage(submission.getId(), fileMessages);

        jmsTemplate.convertAndSend(
            destination,
            message
        );
    }

}
