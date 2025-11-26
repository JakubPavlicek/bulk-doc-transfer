package com.asynchronous.app.service;

import com.asynchronous.app.mapper.SubmissionFileMessageMapper;
import com.asynchronous.app.model.FileMessage;
import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.Submitter;
import com.shared.core.service.DocumentSubmissionService;
import com.shared.core.service.SubmissionService;
import com.shared.core.service.SubmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final DocumentSubmissionService documentSubmissionService;
    private final SubmitterService submitterService;
    private final SubmissionFileMessageMapper submissionFileMapper;
    private final JmsProducer jmsProducer;

    @Override
    public Long createSubmission(String email, String subject, String description, List<MultipartFile> files) {
        log.info("Creating submission...");

        // Save the submitter and submission
        Submitter submitter = submitterService.findOrSaveSubmitter(email);
        DocumentSubmission submission = documentSubmissionService.saveSubmission(subject, description, submitter);

        // Send the submission with files to the queue
        List<FileMessage> fileMessages = submissionFileMapper.toFileMessages(files);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                jmsProducer.sendSubmissionMessage(submission, fileMessages);
            }
        });

        return submission.getId();
    }

}
