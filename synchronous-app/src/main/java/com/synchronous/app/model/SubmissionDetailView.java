package com.synchronous.app.model;

import lombok.Data;

import java.time.Instant;

@Data
public class SubmissionDetailView {

    private Long id;
    private SubmitterView submitter;
    private String referenceNumber;
    private Instant createdAt;
    private String state;
    private String checkResult;

}
