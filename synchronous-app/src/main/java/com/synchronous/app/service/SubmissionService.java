package com.synchronous.app.service;

import com.synchronous.app.model.SubmissionView;
import com.synchronous.app.repository.DocumentSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubmissionService {

    private final DocumentSubmissionRepository submissionRepository;

    public SubmissionView getSubmission(Long submissionId) {
        return submissionRepository.findById(submissionId, SubmissionView.class)
                                   .orElseThrow(RuntimeException::new);
    }

}
