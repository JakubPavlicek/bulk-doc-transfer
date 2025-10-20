package com.synchronous.app.mapper;

import com.synchronous.api.dto.Submission;
import com.synchronous.app.model.SubmissionView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubmissionApiMapper {

    Submission mapToSubmission(SubmissionView submissionView);

}
