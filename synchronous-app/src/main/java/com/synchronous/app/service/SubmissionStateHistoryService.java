package com.synchronous.app.service;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.entity.SubmissionStateHistory;
import com.synchronous.app.repository.SubmissionStateHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmissionStateHistoryService {

    private final SubmissionStateHistoryRepository historyRepository;

    protected void saveStateForSubmission(SubmissionState state, DocumentSubmission submission) {
        SubmissionStateHistory history = SubmissionStateHistory.builder()
                                                               .submission(submission)
                                                               .currentState(state)
                                                               .build();
        historyRepository.save(history);
    }

}
