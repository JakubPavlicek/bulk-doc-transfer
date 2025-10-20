package com.synchronous.app.service;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.SubmissionCheckResult;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.entity.Submitter;
import com.synchronous.app.model.SubmissionDetailView;
import com.synchronous.app.model.SubmissionView;
import com.synchronous.app.repository.DocumentSubmissionRepository;
import com.synchronous.app.util.ReferenceNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionService {

    private final DocumentSubmissionRepository submissionRepository;
    private final SubmissionFileService submissionFileService;
    private final SubmitterService submitterService;

    public SubmissionView getSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId, SubmissionView.class)
                                   .orElseThrow(() -> new RuntimeException("Submission with ID: " + submissionId + " not found"));
    }

    public Page<@NonNull SubmissionDetailView> listSubmissions(String submitterEmail, SubmissionState state, Pageable pageable) {
        return submissionRepository.findAllBySubmitter_EmailAndState(submitterEmail, state, pageable);
    }

    public void uploadSubmission(String email, String subject, String description, List<MultipartFile> files) {
        Submitter submitter = submitterService.findOrSaveSubmitter(email);
        DocumentSubmission submission = DocumentSubmission.builder()
                                                          .submitter(submitter)
                                                          .subject(subject)
                                                          .description(description)
                                                          .referenceNumber(ReferenceNumberGenerator.getNextReferenceNumber())
                                                          .state(SubmissionState.ACCEPTED)
                                                          .build();
        submissionRepository.save(submission);

        // Check files for electronic signature and malware
        checkFiles(submission);
        submission.setState(SubmissionState.PROCESSED);

        // If files are processed, save them
        submissionFileService.saveFiles(files, submission);
        submission.setState(SubmissionState.SAVED);

        // Finally, send a response
        submission.setState(SubmissionState.RESPONSE_SENT);
    }

    /// Checks files for Electronic Signature and Malware
    private void checkFiles(DocumentSubmission submission) {
        try {
            log.info("Processing files...");
            Thread.sleep(SECONDS.toMillis(5)); // Simulate processing delay
            log.info("Files processed");
        } catch (InterruptedException e) {
            throw new RuntimeException("Error processing files");
        }

        submission.setCheckResult(SubmissionCheckResult.OK);
    }

}
