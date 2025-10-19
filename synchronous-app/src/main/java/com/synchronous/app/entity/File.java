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
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "files_id_gen"
    )
    @SequenceGenerator(
        name = "files_id_gen",
        sequenceName = "files_id_seq",
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

    @Size(max = 255)
    @NotNull
    @Column(
        name = "name",
        nullable = false
    )
    private String name;

    @Size(max = 150)
    @NotNull
    @Column(
        name = "type",
        nullable = false,
        length = 150
    )
    private String type;

    @NotNull
    @Column(
        name = "size",
        nullable = false
    )
    private Long size;

    @Column(name = "content")
    private byte[] content;

    @NotNull
    @Column(
        name = "created_at",
        nullable = false
    )
    private Instant createdAt;

}