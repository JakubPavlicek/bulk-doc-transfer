package com.synchronous.app.mapper;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.Submitter;
import com.synchronous.app.model.SubmissionDetailView;
import com.synchronous.app.model.SubmitterView;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface DocumentSubmissionMapper {

    default Page<@NonNull SubmissionDetailView> mapToSubmissionDetailPage(Page<@NonNull DocumentSubmission> documentSubmissions) {
        return documentSubmissions.map(this::toSubmissionDetailView);
    }

    SubmissionDetailView toSubmissionDetailView(DocumentSubmission submission);

    default SubmitterView mapSubmitter(Submitter submitter) {
        return submitter::getEmail;
    }

}
