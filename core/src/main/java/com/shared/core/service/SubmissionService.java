package com.shared.core.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionService {

    void createSubmission(String email, String subject, String description, List<MultipartFile> files);

}
