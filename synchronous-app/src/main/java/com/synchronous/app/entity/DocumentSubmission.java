package com.synchronous.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "document_submissions",
    uniqueConstraints = { @UniqueConstraint(
        name = "document_submissions_reference_number_key",
        columnNames = { "reference_number" }
    ) }
)
public class DocumentSubmission {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "document_submissions_id_gen"
    )
    @SequenceGenerator(
        name = "document_submissions_id_gen",
        sequenceName = "document_submissions_id_seq",
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
        name = "submitter_id",
        nullable = false
    )
    private Submitter submitter;

    @Size(max = 2000)
    @NotNull
    @Column(
        name = "subject",
        nullable = false,
        length = 2000
    )
    private String subject;

    @Column(
        name = "description",
        length = Integer.MAX_VALUE
    )
    private String description;

    @Size(max = 255)
    @NotNull
    @Column(
        name = "reference_number",
        nullable = false
    )
    private String referenceNumber;

    @Column(
        name = "created_at",
        updatable = false
    )
    @CreationTimestamp
    private Instant createdAt;

    @Column(
        name = "state",
        length = 13
    )
    @Enumerated(EnumType.STRING)
    private SubmissionState state;

    @Column(
        name = "check_result",
        length = 20
    )
    @Enumerated(EnumType.STRING)
    private SubmissionCheckResult checkResult;

    @Column(name = "total_files")
    private Integer totalFiles;

    @OneToMany(mappedBy = "submission")
    @OrderBy(SubmissionFile_.ID)
    private final Set<SubmissionFile> files = new LinkedHashSet<>();

    @OneToMany(mappedBy = "submission")
    private final Set<SubmissionStateHistory> submissionStateHistories = new LinkedHashSet<>();

}