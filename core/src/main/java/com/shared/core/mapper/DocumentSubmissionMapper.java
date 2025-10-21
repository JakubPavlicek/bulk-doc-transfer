package com.shared.core.mapper;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.Submitter;
import com.shared.core.model.SubmissionDetailView;
import com.shared.core.model.SubmitterView;
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
