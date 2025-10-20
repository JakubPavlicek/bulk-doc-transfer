package com.synchronous.app.model;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class SubmissionView {

    private Long id;
    private SubmitterView submitter;
    private String subject;
    private String description;
    private String referenceNumber;
    private Instant createdAt;
    private String state;
    private String checkResult;
    private List<FileView> files;

}