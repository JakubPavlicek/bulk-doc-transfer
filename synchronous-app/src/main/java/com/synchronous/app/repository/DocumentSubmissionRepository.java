package com.synchronous.app.repository;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.model.SubmissionView;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

import static com.synchronous.app.entity.DocumentSubmission_.FILES;
import static com.synchronous.app.entity.DocumentSubmission_.SUBMITTER;

public interface DocumentSubmissionRepository extends JpaRepository<@NonNull DocumentSubmission, @NonNull Long>, JpaSpecificationExecutor<@NonNull DocumentSubmission> {

    @EntityGraph(attributePaths = { SUBMITTER, FILES })
    Optional<SubmissionView> findDocumentSubmissionById(@NonNull Long id);

}