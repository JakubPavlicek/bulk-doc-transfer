package com.synchronous.app.repository.specification;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.DocumentSubmission_;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.entity.Submitter;
import com.synchronous.app.entity.Submitter_;
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
