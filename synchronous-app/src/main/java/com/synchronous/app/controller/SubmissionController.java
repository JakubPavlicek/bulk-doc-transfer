package com.synchronous.app.controller;

import com.synchronous.api.SubmissionsApi;
import com.synchronous.api.dto.Submission;
import com.synchronous.api.dto.SubmissionDetail;
import com.synchronous.api.dto.SubmissionState;
import com.synchronous.app.mapper.SubmissionApiMapper;
import com.synchronous.app.model.SubmissionView;
import com.synchronous.app.service.SubmissionService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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
        SubmissionView submissionView = submissionService.getSubmission(submissionId);
        Submission submission = submissionApiMapper.mapToSubmission(submissionView);
        return ResponseEntity.ok(submission);
    }

    @Override
    public ResponseEntity<@NonNull List<SubmissionDetail>> listSubmissions(
        @Nullable Email submitterEmail,
        @Nullable SubmissionState state,
        Pageable pageable
    ) {
        return SubmissionsApi.super.listSubmissions(submitterEmail, state, pageable);
    }

    @Override
    public ResponseEntity<@NonNull Void> uploadSubmission(
        String email,
        String subject,
        List<MultipartFile> files,
        String description
    ) {
        return SubmissionsApi.super.uploadSubmission(email, subject, files, description);
    }

}
