package com.shared.core.repository;

import com.shared.core.entity.Submitter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmitterRepository extends JpaRepository<@NonNull Submitter, @NonNull Long> {

    Optional<Submitter> findByEmail(@NonNull String email);

}