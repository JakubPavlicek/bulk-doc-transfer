package com.synchronous.app.service;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.model.SubmissionDetailView;
import com.synchronous.app.model.SubmissionView;
import com.synchronous.app.repository.DocumentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubmissionService {

    private final DocumentSubmissionRepository submissionRepository;

    public SubmissionView getSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId, SubmissionView.class)
                                   .orElseThrow(RuntimeException::new);
    }

    public Page<@NonNull SubmissionDetailView> listSubmissions(
        String submitterEmail,
        SubmissionState state,
        Pageable pageable
    ) {
        return submissionRepository.findAllBySubmitter_EmailAndState(submitterEmail, state, pageable);
    }

    public void uploadSubmission(
        String email,
        String subject,
        String description,
        List<MultipartFile> files
    ) {
        // TODO: If submitter does not exist, create it.

        DocumentSubmission submission = DocumentSubmission.builder()
                                                          .build();
    }

}
