package com.synchronous.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "submission_state_history")
public class SubmissionStateHistory {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "submission_state_history_id_gen"
    )
    @SequenceGenerator(
        name = "submission_state_history_id_gen",
        sequenceName = "submission_state_history_id_seq",
        allocationSize = 1
    )
    @Column(
        name = "id",
        nullable = false
    )
    private Long id;

    @NotNull
    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    @JoinColumn(
        name = "submission_id",
        nullable = false
    )
    private DocumentSubmission submission;

    @Column(
        name = "current_state",
        columnDefinition = "submission_state not null"
    )
    private Object currentState;

    @NotNull
    @Column(
        name = "changed_at",
        nullable = false
    )
    private Instant changedAt;

}