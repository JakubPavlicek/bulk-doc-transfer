package com.shared.core.exception;

import lombok.Getter;

@Getter
public class SubmissionFileSaveException extends RuntimeException {

    private final String filename;

    public SubmissionFileSaveException(String filename) {
        super("Error saving file: " + filename);
        this.filename = filename;
    }

}
