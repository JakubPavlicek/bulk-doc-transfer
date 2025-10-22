package com.shared.core.exception;

import lombok.Getter;

@Getter
public class SubmissionFileReadException extends RuntimeException {

    private final String filename;

    public SubmissionFileReadException(String filename) {
        super("Error reading file: " + filename);
        this.filename = filename;
    }

}
