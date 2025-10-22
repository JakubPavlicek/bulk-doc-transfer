package com.asynchronous.app.mapper;

import com.asynchronous.app.model.FileMessage;
import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionFile;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionFileMessageMapper {

    default SubmissionFile toSubmissionFile(FileMessage file, @Context DocumentSubmission submission) {
        return SubmissionFile.builder()
                             .submission(submission)
                             .name(file.name())
                             .type(file.type())
                             .size(file.size())
                             .content(file.content())
                             .build();
    }

    List<SubmissionFile> toSubmissionFiles(List<FileMessage> files, @Context DocumentSubmission submission);

    default FileMessage toFileMessage(MultipartFile file) {
        try {
            return FileMessage.builder()
                              .name(file.getOriginalFilename())
                              .type(file.getContentType())
                              .size(file.getSize())
                              .content(file.getBytes())
                              .build();
        } catch (IOException e) {
            throw new RuntimeException("Could not read file: " + file.getOriginalFilename(), e);
        }
    }

    List<FileMessage> toFileMessages(List<MultipartFile> submissionFiles);

}
