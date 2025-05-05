package com.example.finmonitor.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class BankResponse {
    private UUID id;
    private String name;

    public BankResponse() {
    }

    public BankResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

}