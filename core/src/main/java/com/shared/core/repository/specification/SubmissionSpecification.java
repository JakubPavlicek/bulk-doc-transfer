package com.shared.core.repository.specification;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.DocumentSubmission_;
import com.shared.core.entity.SubmissionState;
import com.shared.core.entity.Submitter;
import com.shared.core.entity.Submitter_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

public class SubmissionSpecification {

    private SubmissionSpecification() {
    }

    public static Specification<@NonNull DocumentSubmission> fetchSubmitter() {
        return (root, query, criteriaBuilder) -> {
            if (!query.getResultType().equals(Long.class)) {
                root.fetch(DocumentSubmission_.SUBMITTER, JoinType.INNER);
            }
            return null;
        };
    }

    public static Specification<@NonNull DocumentSubmission> hasSubmitterEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            Join<DocumentSubmission, Submitter> submitterJoin = root.join(DocumentSubmission_.SUBMITTER);
            return criteriaBuilder.equal(submitterJoin.get(Submitter_.EMAIL), email);
        };
    }

    public static Specification<@NonNull DocumentSubmission> hasState(SubmissionState state) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(DocumentSubmission_.STATE), state);
        };
    }

}
