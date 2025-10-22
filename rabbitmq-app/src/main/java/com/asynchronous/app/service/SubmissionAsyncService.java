package com.asynchronous.app.service;

import com.asynchronous.app.model.FileMessage;
import com.shared.core.entity.DocumentSubmission;
import com.shared.core.service.DocumentSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubmissionAsyncService {

    private final DocumentSubmissionService documentSubmissionService;
    private final RabbitProducer rabbitProducer;

    @Async
    protected void processSubmission(DocumentSubmission submission, List<FileMessage> files) {
        // Check files for electronic signature and malware
        documentSubmissionService.checkFiles(submission);

        // Send the submission with files to the queue
        rabbitProducer.sendSubmissionMessage(submission, files);
    }

}
