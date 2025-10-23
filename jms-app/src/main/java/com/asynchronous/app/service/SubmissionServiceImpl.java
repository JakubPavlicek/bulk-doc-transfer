package com.asynchronous.app.service;

import com.asynchronous.app.mapper.SubmissionFileMessageMapper;
import com.asynchronous.app.model.FileMessage;
import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.Submitter;
import com.shared.core.service.DocumentSubmissionService;
import com.shared.core.service.SubmissionService;
import com.shared.core.service.SubmitterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final DocumentSubmissionService documentSubmissionService;
    private final SubmitterService submitterService;
    private final SubmissionAsyncService submissionAsyncService;
    private final SubmissionFileMessageMapper submissionFileMapper;

    @Override
    public Long createSubmission(String email, String subject, String description, List<MultipartFile> files) {
        log.info("Creating submission...");

        // Save the submitter and submission
        Submitter submitter = submitterService.findOrSaveSubmitter(email);
        DocumentSubmission submission = documentSubmissionService.saveSubmission(subject, description, submitter);

        // Convert MultipartFile to FileMessage eagerly to avoid NoSuchFileException in async processing
        List<FileMessage> fileMessages = submissionFileMapper.toFileMessages(files);

        // Process the submission asynchronously
        submissionAsyncService.processSubmission(submission, fileMessages);

        return submission.getId();
    }

}
