package com.synchronous.app.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class SubmissionDetailView {

    private Long id;
    private SubmitterView submitter;
    private String referenceNumber;
    private Instant createdAt;
    private String state;
    private String checkResult;

}
