package com.asynchronous.app.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record FileMessage(
    String name,
    String type,
    Long size,
    byte[] content
) implements Serializable {

}
