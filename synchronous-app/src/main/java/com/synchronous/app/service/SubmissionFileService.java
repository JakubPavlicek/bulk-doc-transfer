package com.synchronous.app.service;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.SubmissionFile;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.repository.SubmissionFileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionFileService {

    private final SubmissionFileRepository submissionFileRepository;

    private final SubmissionStateHistoryService stateHistoryService;

    protected void saveFiles(List<MultipartFile> files, DocumentSubmission submission) {
        for (MultipartFile file : files) {
            try {
                log.info("Saving file: {} for submission ID: {}", file.getOriginalFilename(), submission.getId());
                SubmissionFile submissionFile = SubmissionFile.builder()
                                                              .submission(submission)
                                                              .name(file.getOriginalFilename())
                                                              .type(file.getContentType())
                                                              .size(file.getSize())
                                                              .content(file.getBytes())
                                                              .build();
                submissionFileRepository.save(submissionFile);
            } catch (IOException e) {
                throw new RuntimeException("Error saving file: " + file.getOriginalFilename());
            }
        }

        submission.setTotalFiles(files.size());
        submission.setState(SubmissionState.SAVED);
        stateHistoryService.saveStateForSubmission(SubmissionState.SAVED, submission);
    }

}
