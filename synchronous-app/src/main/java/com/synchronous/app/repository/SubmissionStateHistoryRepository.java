package com.synchronous.app.repository;

import com.synchronous.app.entity.SubmissionStateHistory;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionStateHistoryRepository extends JpaRepository<@NonNull SubmissionStateHistory, @NonNull Long> {

}