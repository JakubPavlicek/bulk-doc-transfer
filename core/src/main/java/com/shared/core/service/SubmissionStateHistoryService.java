package com.shared.core.service;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionState;
import com.shared.core.entity.SubmissionStateHistory;
import com.shared.core.repository.SubmissionStateHistoryRepository;
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

    public void saveStateForSubmission(SubmissionState state, DocumentSubmission submission) {
        SubmissionStateHistory history = SubmissionStateHistory.builder()
                                                               .submission(submission)
                                                               .currentState(state)
                                                               .build();
        historyRepository.save(history);
    }

}
