package com.shared.core.exception;

import lombok.Getter;

@Getter
public class SubmissionNotFoundException extends RuntimeException {

    private final Long submissionId;

    public SubmissionNotFoundException(Long submissionId) {
        super("Submission with ID: " + submissionId + " not found");
        this.submissionId = submissionId;
    }

}
