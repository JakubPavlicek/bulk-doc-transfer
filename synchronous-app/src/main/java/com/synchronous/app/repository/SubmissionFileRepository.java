package com.synchronous.app.repository;

import com.synchronous.app.entity.SubmissionFile;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionFileRepository extends JpaRepository<@NonNull SubmissionFile, @NonNull Long> {

}