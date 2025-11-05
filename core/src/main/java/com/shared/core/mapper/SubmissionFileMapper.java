package com.shared.core.mapper;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionFile;
import com.shared.core.exception.FileProcessingEexception;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface SubmissionFileMapper {

    default SubmissionFile toSubmissionFile(MultipartFile file, DocumentSubmission submission) {
        try {
            return SubmissionFile.builder()
                                 .submission(submission)
                                 .name(file.getOriginalFilename())
                                 .type(file.getContentType())
                                 .size(file.getSize())
                                 .content(file.getBytes())
                                 .build();
        } catch (IOException e) {
            throw new FileProcessingEexception("Could not read file: " + file.getOriginalFilename(), e);
        }
    }

}
