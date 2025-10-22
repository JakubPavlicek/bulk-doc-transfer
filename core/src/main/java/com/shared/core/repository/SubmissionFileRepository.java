package com.shared.core.repository;

import com.shared.core.entity.DocumentSubmission;
import com.shared.core.entity.SubmissionFile;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionFileRepository extends JpaRepository<@NonNull SubmissionFile, @NonNull Long> {

    Optional<SubmissionFile> findBySubmissionAndId(DocumentSubmission submission, Long id);

}