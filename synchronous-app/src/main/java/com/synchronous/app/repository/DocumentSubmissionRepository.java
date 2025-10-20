package com.synchronous.app.repository;

import com.synchronous.app.entity.DocumentSubmission;
import com.synchronous.app.entity.SubmissionState;
import com.synchronous.app.model.SubmissionDetailView;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentSubmissionRepository extends JpaRepository<@NonNull DocumentSubmission, @NonNull Long> {

    <T> Optional<T> findById(@NonNull Long id, Class<T> clazz);

    Page<@NonNull SubmissionDetailView> findAllBySubmitter_EmailAndState(
        String submitterEmail, SubmissionState state, Pageable pageable
    );

}