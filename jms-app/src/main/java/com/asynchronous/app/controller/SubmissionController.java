package com.asynchronous.app.controller;

import com.asynchronous.api.SubmissionsApi;
import com.asynchronous.api.dto.DocumentSubmissionState;
import com.asynchronous.api.dto.Submission;
import com.asynchronous.api.dto.SubmissionPage;
import com.asynchronous.api.dto.SubmissionResponse;
import com.asynchronous.app.mapper.SubmissionApiMapper;
import com.shared.core.entity.SubmissionFile;
import com.shared.core.entity.SubmissionState;
import com.shared.core.exception.SubmissionFileReadException;
import com.shared.core.model.SubmissionDetailView;
import com.shared.core.model.SubmissionView;
import com.shared.core.service.DocumentSubmissionService;
import com.shared.core.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class SubmissionController implements SubmissionsApi {

    private final DocumentSubmissionService documentSubmissionService;
    private final SubmissionService submissionService;
    private final SubmissionApiMapper submissionApiMapper;

    @Override
    public ResponseEntity<@NonNull Submission> getSubmission(Long submissionId) {
        log.info("Submission requested for ID: {}", submissionId);

        SubmissionView submissionView = documentSubmissionService.getSubmission(submissionId);
        Submission submission = submissionApiMapper.mapToSubmission(submissionView);

        return ResponseEntity.ok(submission);
    }

    @Override
    public ResponseEntity<@NonNull Void> deleteSubmissions() {
        documentSubmissionService.deleteSubmissions();

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<@NonNull Resource> downloadSubmissionFile(Long submissionId, Long fileId) {
        SubmissionFile file = documentSubmissionService.getSubmissionFile(submissionId, fileId);
        Resource resource = new ByteArrayResource(file.getContent());

        try {
            return ResponseEntity.ok()
                                 .contentType(MediaType.parseMediaType(file.getType()))
                                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                                 .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
                                 .body(resource);
        } catch (IOException e) {
            throw new SubmissionFileReadException(file.getName());
        }
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
            documentSubmissionService.listSubmissions(submitterEmail, submissionState, pageable);
        SubmissionPage submissionPage = submissionApiMapper.mapToSubmissionPage(submissionDetailViewPage);

        return ResponseEntity.ok(submissionPage);
    }

    @Override
    public ResponseEntity<@NonNull SubmissionResponse> createSubmission(
        String email,
        String subject,
        List<MultipartFile> files,
        String description
    ) {
        log.info("Submission creation requested for email: {}, number of files: {}", email, files.size());

        Long submissionId = submissionService.createSubmission(email, subject, description, files);
        SubmissionResponse response = new SubmissionResponse(submissionId);

        return ResponseEntity.accepted().body(response);
    }

}
