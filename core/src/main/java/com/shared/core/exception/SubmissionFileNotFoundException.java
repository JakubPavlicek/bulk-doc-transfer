package com.shared.core.exception;

import lombok.Getter;

@Getter
public class SubmissionFileNotFoundException extends RuntimeException {

    private final Long submissionId;
    private final Long fileId;

    public SubmissionFileNotFoundException(Long submissionId, Long fileId) {
        super("File with ID: " + fileId + " not found in submission with ID: " + submissionId);
        this.submissionId = submissionId;
        this.fileId = fileId;
    }

}
