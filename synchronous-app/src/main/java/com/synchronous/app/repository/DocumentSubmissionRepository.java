package com.synchronous.app.repository;

import com.synchronous.app.entity.DocumentSubmission;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentSubmissionRepository extends JpaRepository<@NonNull DocumentSubmission, @NonNull Long> {

    <T> Optional<T> findById(@NonNull Long id, Class<T> clazz);

}