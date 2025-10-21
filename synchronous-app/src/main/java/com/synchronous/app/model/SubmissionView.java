package com.synchronous.app.model;

import java.time.Instant;
import java.util.List;

public interface SubmissionView {

    Long getId();
    SubmitterView getSubmitter();
    String getSubject();
    String getDescription();
    String getReferenceNumber();
    Instant getCreatedAt();
    String getState();
    String getCheckResult();
    List<FileView> getFiles();

}