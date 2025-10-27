package com.shared.core.repository;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.model.SubmissionView;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.Optional;

import static com.shared.core.entity.DocumentSubmission_.FILES;
import static com.shared.core.entity.DocumentSubmission_.SUBMITTER;

public interface DocumentSubmissionRepository extends JpaRepository<@NonNull DocumentSubmission, @NonNull Long>, JpaSpecificationExecutor<@NonNull DocumentSubmission> {

    @EntityGraph(attributePaths = { SUBMITTER, FILES })
    Optional<SubmissionView> findDocumentSubmissionById(@NonNull Long id);

    @Modifying
    @NativeQuery("""
        ALTER SEQUENCE document_submissions_id_seq RESTART WITH 1;
        ALTER SEQUENCE files_id_seq RESTART WITH 1;
        ALTER SEQUENCE submission_state_history_id_seq RESTART WITH 1;
        ALTER SEQUENCE submitters_id_seq RESTART WITH 1;
    """)
    void resetSequences();

}