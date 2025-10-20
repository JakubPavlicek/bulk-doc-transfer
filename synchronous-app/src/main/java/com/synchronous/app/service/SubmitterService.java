package com.synchronous.app.service;

import com.synchronous.app.entity.Submitter;
import com.synchronous.app.repository.SubmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubmitterService {

    private final SubmitterRepository submitterRepository;

    protected Submitter findOrSaveSubmitter(String submitterEmail) {
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

}
