package com.example.finmonitor.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class CategoryResponse {
    private UUID id;
    private String name;

    public CategoryResponse() {
    }

    public CategoryResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

}