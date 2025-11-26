package com.shared.core.service;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionFile;
import com.shared.core.entity.SubmissionState;
import com.shared.core.exception.SubmissionFileNotFoundException;
import com.shared.core.mapper.SubmissionFileMapper;
import com.shared.core.repository.SubmissionFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionFileService {

    private final SubmissionFileRepository submissionFileRepository;
    private final SubmissionStateHistoryService stateHistoryService;
    private final SubmissionFileMapper submissionFileMapper;

    public <T> void saveFiles(List<T> files, DocumentSubmission submission) {
        log.info("Saving {} files for submission ID: {}", files.size(), submission.getId());

        List<SubmissionFile> toSave = new ArrayList<>(files.size());

        files.forEach(file -> {
            switch (file) {
                case MultipartFile mf -> toSave.add(submissionFileMapper.toSubmissionFile(mf, submission));
                case SubmissionFile sf -> toSave.add(sf);
                default -> throw new IllegalStateException("Unexpected value: " + file);
            }
        });

        submissionFileRepository.saveAll(toSave);

        submission.setTotalFiles(files.size());
        submission.setState(SubmissionState.SAVED);
        stateHistoryService.saveStateForSubmission(SubmissionState.SAVED, submission);
    }

    protected SubmissionFile getSubmissionFile(DocumentSubmission submission, Long fileId) {
        return submissionFileRepository.findBySubmissionAndId(submission, fileId)
                                       .orElseThrow(() -> new SubmissionFileNotFoundException(submission.getId(), fileId));
    }

    public void deleteAllInBatch() {
        submissionFileRepository.deleteAllInBatch();
    }

}
