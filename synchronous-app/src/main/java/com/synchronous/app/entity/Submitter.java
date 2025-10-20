package com.synchronous.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
    name = "submitters",
    uniqueConstraints = { @UniqueConstraint(
        name = "submitters_email_key",
        columnNames = { "email" }
    ) }
)
public class Submitter {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "submitters_id_gen"
    )
    @SequenceGenerator(
        name = "submitters_id_gen",
        sequenceName = "submitters_id_seq",
        allocationSize = 1
    )
    @Column(
        name = "id",
        nullable = false
    )
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(
        name = "email",
        nullable = false
    )
    private String email;

    @OneToMany(mappedBy = "submitter")
    private Set<DocumentSubmission> documentSubmissions = new LinkedHashSet<>();

}