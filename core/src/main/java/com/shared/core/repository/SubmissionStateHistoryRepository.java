package com.shared.core.repository;

import com.shared.core.entity.SubmissionStateHistory;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionStateHistoryRepository extends JpaRepository<@NonNull SubmissionStateHistory, @NonNull Long> {

}