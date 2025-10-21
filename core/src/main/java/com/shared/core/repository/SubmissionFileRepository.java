package com.shared.core.repository;

import com.shared.core.entity.SubmissionFile;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionFileRepository extends JpaRepository<@NonNull SubmissionFile, @NonNull Long> {

}