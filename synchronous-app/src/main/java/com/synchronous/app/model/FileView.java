package com.synchronous.app.model;

import java.time.Instant;

public interface FileView {

    Long getId();
    String getName();
    String getType();
    Long getSize();
    Instant getCreatedAt();

}