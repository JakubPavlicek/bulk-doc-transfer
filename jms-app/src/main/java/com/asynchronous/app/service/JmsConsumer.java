package com.asynchronous.app.service;

import com.asynchronous.app.mapper.SubmissionFileMessageMapper;
import com.asynchronous.app.model.FileMessage;
import com.asynchronous.app.model.SubmissionMessage;
import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionFile;
import com.shared.core.entity.SubmissionState;
import com.shared.core.service.DocumentSubmissionService;
import com.shared.core.service.SubmissionFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class JmsConsumer {

    private final DocumentSubmissionService documentSubmissionService;
    private final SubmissionFileService submissionFileService;
    private final SubmissionFileMessageMapper submissionFileMapper;

    @JmsListener(destination = "${spring.jms.jndi-name}")
    public void receiveSubmissionMessage(SubmissionMessage message) {
        Long submissionId = message.submissionId();
        List<FileMessage> files = message.files();

        log.info("Received message for submission ID: {} with {} files", submissionId, files.size());

        DocumentSubmission submission = documentSubmissionService.findSubmissionById(submissionId);
        List<SubmissionFile> submissionFiles = submissionFileMapper.toSubmissionFiles(files, submission);

        // Save the files
        submissionFileService.saveFiles(submissionFiles, submission);

        // Finally, update the submission state
        documentSubmissionService.updateSubmissionState(submission, SubmissionState.RESPONSE_SENT);

        log.info("Submission with ID: {} processed successfully", submissionId);
    }

}
