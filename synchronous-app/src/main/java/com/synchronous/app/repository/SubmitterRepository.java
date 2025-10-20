package com.synchronous.app.repository;

import com.synchronous.app.entity.Submitter;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmitterRepository extends JpaRepository<@NonNull Submitter, @NonNull Long> {

    Optional<Submitter> findByEmail(@NonNull String email);

}