package com.synchronous.app.model;

import lombok.Data;

import java.time.Instant;

@Data
public class FileView {

    private Long id;
    private String name;
    private String type;
    private Long size;
    private Instant createdAt;

}