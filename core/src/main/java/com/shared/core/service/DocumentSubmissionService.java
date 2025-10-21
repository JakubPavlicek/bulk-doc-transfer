package com.shared.core.service;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionCheckResult;
import com.shared.core.entity.SubmissionState;
import com.shared.core.entity.Submitter;
import com.shared.core.mapper.DocumentSubmissionMapper;
import com.shared.core.model.SubmissionDetailView;
import com.shared.core.model.SubmissionView;
import com.shared.core.repository.DocumentSubmissionRepository;
import com.shared.core.repository.specification.SubmissionSpecification;
import com.shared.core.util.ReferenceNumberGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class DocumentSubmissionService {

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

    public DocumentSubmission saveSubmission(String subject, String description, Submitter submitter) {
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
    public void checkFiles(DocumentSubmission submission) {
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
