package com.synchronous.app.model;

import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
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