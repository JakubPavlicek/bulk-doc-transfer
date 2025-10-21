package com.synchronous.app.service;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.SubmissionCheckResult;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.entity.Submitter;
import com.synchronous.app.mapper.DocumentSubmissionMapper;
import com.synchronous.app.model.SubmissionDetailView;
import com.synchronous.app.model.SubmissionView;
import com.synchronous.app.repository.DocumentSubmissionRepository;
import com.synchronous.app.repository.specification.SubmissionSpecification;
import com.synchronous.app.util.ReferenceNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    private final SubmissionStateHistoryService stateHistoryService;
    private final SubmitterService submitterService;
    private final DocumentSubmissionMapper submissionMapper;

    public SubmissionView getSubmission(Long submissionId) {
        return submissionRepository.findDocumentSubmissionById(submissionId)
                                   .orElseThrow(() -> new RuntimeException("Submission with ID: " + submissionId + " not found"));
    }

    public Page<@NonNull SubmissionDetailView> listSubmissions(String submitterEmail, SubmissionState state, Pageable pageable) {
        Specification<@NonNull DocumentSubmission> spec = Specification.where(SubmissionSpecification.fetchSubmitter());

        if (submitterEmail != null) {
            spec = spec.and(SubmissionSpecification.hasSubmitterEmail(submitterEmail));
        }
        if (state != null) {
            spec = spec.and(SubmissionSpecification.hasState(state));
        }

        Page<@NonNull DocumentSubmission> documentSubmissions = submissionRepository.findAll(spec, pageable);
        return submissionMapper.mapToSubmissionDetailPage(documentSubmissions);
    }

    public void createSubmission(String email, String subject, String description, List<MultipartFile> files) {
        log.info("Creating submission...");

        // Save the submitter and submission
        Submitter submitter = submitterService.findOrSaveSubmitter(email);
        DocumentSubmission submission = saveSubmission(subject, description, submitter);

        // Check files for electronic signature and malware
        checkFiles(submission);

        // If files are processed, save them
        submissionFileService.saveFiles(files, submission);

        // Finally, send a response
        submission.setState(SubmissionState.RESPONSE_SENT);
        stateHistoryService.saveStateForSubmission(SubmissionState.RESPONSE_SENT, submission);

        log.info("Submission created successfully");
    }

    private DocumentSubmission saveSubmission(String subject, String description, Submitter submitter) {
        DocumentSubmission submission = DocumentSubmission.builder()
                                                          .submitter(submitter)
                                                          .subject(subject)
                                                          .description(description)
                                                          .referenceNumber(ReferenceNumberGenerator.getNextReferenceNumber())
                                                          .state(SubmissionState.ACCEPTED)
                                                          .build();
        submissionRepository.save(submission);
        stateHistoryService.saveStateForSubmission(SubmissionState.ACCEPTED, submission);

        return submission;
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
        submission.setState(SubmissionState.PROCESSED);
        stateHistoryService.saveStateForSubmission(SubmissionState.PROCESSED, submission);
    }

}
