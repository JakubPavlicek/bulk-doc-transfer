package com.shared.core.mapper;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionState;
import com.shared.core.entity.Submitter;
import com.shared.core.model.SubmissionDetailView;
import com.shared.core.model.SubmitterView;
import com.shared.core.util.ReferenceNumberGenerator;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface DocumentSubmissionMapper {

    default DocumentSubmission toDocumentSubmission(String subject, String description, Submitter submitter) {
        return DocumentSubmission.builder()
                                 .submitter(submitter)
                                 .subject(subject)
                                 .description(description)
                                 .referenceNumber(ReferenceNumberGenerator.getNextReferenceNumber())
                                 .state(SubmissionState.ACCEPTED)
                                 .build();
    }

    default Page<@NonNull SubmissionDetailView> mapToSubmissionDetailPage(Page<@NonNull DocumentSubmission> documentSubmissions) {
        return documentSubmissions.map(this::toSubmissionDetailView);
    }

    SubmissionDetailView toSubmissionDetailView(DocumentSubmission submission);

    default SubmitterView mapSubmitter(Submitter submitter) {
        return submitter::getEmail;
    }

}
