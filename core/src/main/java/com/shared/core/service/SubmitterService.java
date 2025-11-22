package com.shared.core.service;

import com.shared.core.entity.Submitter;
import com.shared.core.repository.SubmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubmitterService {

    private final SubmitterRepository submitterRepository;

    public Submitter findOrSaveSubmitter(String submitterEmail) {
        log.info("Finding submitter with email: {}", submitterEmail);

        return submitterRepository.findByEmail(submitterEmail)
                                  .orElseGet(() -> saveSubmitter(submitterEmail));
    }

    private Submitter saveSubmitter(String submitterEmail) {
        log.info("Saving submitter with email: {}", submitterEmail);

        Submitter submitter = new Submitter();
        submitter.setEmail(submitterEmail);

        return submitterRepository.save(submitter);
    }

    public void deleteAllInBatch() {
        submitterRepository.deleteAllInBatch();
    }

}
