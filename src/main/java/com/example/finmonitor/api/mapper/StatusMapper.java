package com.example.finmonitor.api.mapper;

import com.example.finmonitor.api.dto.StatusResponse;
import com.example.finmonitor.domain.model.Status;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {
    public StatusResponse toResponse(Status status) {
        return new StatusResponse(status.getId(), status.getName());
    }
}
