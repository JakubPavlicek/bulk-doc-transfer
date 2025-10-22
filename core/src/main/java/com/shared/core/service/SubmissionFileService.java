package com.shared.core.service;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionFile;
import com.shared.core.entity.SubmissionState;
import com.shared.core.exception.SubmissionFileNotFoundException;
import com.shared.core.exception.SubmissionFileSaveException;
import com.shared.core.repository.SubmissionFileRepository;
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

    public void saveFiles(List<MultipartFile> files, DocumentSubmission submission) {
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
                throw new SubmissionFileSaveException(file.getOriginalFilename());
            }
        }

        submission.setTotalFiles(files.size());
        submission.setState(SubmissionState.SAVED);
        stateHistoryService.saveStateForSubmission(SubmissionState.SAVED, submission);
    }

    protected SubmissionFile getSubmissionFile(DocumentSubmission submission, Long fileId) {
        return submissionFileRepository.findBySubmissionAndId(submission, fileId)
                                       .orElseThrow(() -> new SubmissionFileNotFoundException(submission.getId(), fileId));
    }

}
