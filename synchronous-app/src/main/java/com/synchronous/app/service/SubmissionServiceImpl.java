package com.synchronous.app.service;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionState;
import com.shared.core.entity.Submitter;
import com.shared.core.service.DocumentSubmissionService;
import com.shared.core.service.SubmissionFileService;
import com.shared.core.service.SubmissionService;
import com.shared.core.service.SubmitterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final DocumentSubmissionService documentSubmissionService;
    private final SubmissionFileService submissionFileService;
    private final SubmitterService submitterService;

    @Override
    public Long createSubmission(String email, String subject, String description, List<MultipartFile> files) {
        log.info("Creating submission...");

        // Save the submitter and submission
        Submitter submitter = submitterService.findOrSaveSubmitter(email);
        DocumentSubmission submission = documentSubmissionService.saveSubmission(subject, description, submitter);

        // Check files for electronic signature and malware
        documentSubmissionService.checkFiles(submission);

        // If files are processed, save them
        submissionFileService.saveFiles(files, submission);

        // Finally, send a response
        documentSubmissionService.updateSubmissionState(submission, SubmissionState.RESPONSE_SENT);
        submission.setSavedAt(Instant.now());

        log.info("Submission created successfully");

        return submission.getId();
    }

}
