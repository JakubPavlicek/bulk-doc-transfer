package com.asynchronous.app.model;

import java.io.Serializable;
import java.util.List;

public record SubmissionMessage(
    Long submissionId,
    List<FileMessage> files
) implements Serializable {

}
