package com.synchronous.app.mapper;

import com.synchronous.api.dto.Submission;
import com.synchronous.api.dto.SubmissionPage;
import com.shared.core.model.SubmissionDetailView;
import com.shared.core.model.SubmissionView;
import com.shared.core.model.SubmitterView;
import org.jspecify.annotations.NonNull;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface SubmissionApiMapper {

    Submission mapToSubmission(SubmissionView submissionView);

    SubmissionPage mapToSubmissionPage(Page<@NonNull SubmissionDetailView> submissionDetailViewPage);

    default String mapSubmitter(SubmitterView submitter) {
        return submitter.getEmail();
    }

}
