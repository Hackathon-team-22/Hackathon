package com.example.finmonitor.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class StatusResponse {
    private UUID id;
    private String name;

    public StatusResponse() {
    }

    public StatusResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

}