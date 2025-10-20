package com.synchronous.app.controller;

import com.synchronous.api.SubmissionsApi;
import com.synchronous.api.dto.DocumentSubmissionState;
import com.synchronous.api.dto.Submission;
import com.synchronous.api.dto.SubmissionPage;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.mapper.SubmissionApiMapper;
import com.synchronous.app.model.SubmissionDetailView;
import com.synchronous.app.model.SubmissionView;
import com.synchronous.app.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SubmissionController implements SubmissionsApi {

    private final SubmissionService submissionService;
    private final SubmissionApiMapper submissionApiMapper;

    @Override
    public ResponseEntity<@NonNull Submission> getSubmission(Long submissionId) {
        log.info("Submission requested for ID: {}", submissionId);

        SubmissionView submissionView = submissionService.getSubmission(submissionId);
        Submission submission = submissionApiMapper.mapToSubmission(submissionView);

        return ResponseEntity.ok(submission);
    }

    @Override
    public ResponseEntity<@NonNull SubmissionPage> listSubmissions(
        @Nullable String submitterEmail,
        @Nullable DocumentSubmissionState state,
        Pageable pageable
    ) {
        log.info("Submission list requested for submitter email: {}, state: {}", submitterEmail, state);

        SubmissionState submissionState = state != null ? SubmissionState.valueOf(state.name()) : null;

        Page<@NonNull SubmissionDetailView> submissionDetailViewPage =
            submissionService.listSubmissions(submitterEmail, submissionState, pageable);
        SubmissionPage submissionPage = submissionApiMapper.mapToSubmissionPage(submissionDetailViewPage);

        return ResponseEntity.ok(submissionPage);
    }

    @Override
    public ResponseEntity<@NonNull Void> uploadSubmission(
        String email,
        String subject,
        List<MultipartFile> files,
        String description
    ) {
        log.info("Submission upload requested for email: {}, number of files: {}", email, files.size());

        submissionService.uploadSubmission(email, subject, description, files);

        return ResponseEntity.accepted().build();
    }

}
