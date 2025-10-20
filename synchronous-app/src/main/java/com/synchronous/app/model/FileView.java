package com.synchronous.app.model;

import lombok.Getter;

import java.time.Instant;

@Getter
public class FileView {

    private Long id;
    private String name;
    private String type;
    private Long size;
    private Instant createdAt;

}