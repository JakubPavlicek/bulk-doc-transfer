package com.shared.core.model;

import java.time.Instant;
import java.util.List;

public interface SubmissionView {

    Long getId();
    SubmitterView getSubmitter();
    String getSubject();
    String getDescription();
    String getReferenceNumber();
    Instant getCreatedAt();
    Instant getSavedAt();
    String getState();
    String getCheckResult();
    List<FileView> getFiles();

}