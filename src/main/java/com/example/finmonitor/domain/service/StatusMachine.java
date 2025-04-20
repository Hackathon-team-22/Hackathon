package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.Status;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StatusMachine {
    private final Set<String> validStatuses = Set.of(
            "New", "Confirmed", "Processing", "Cancelled", "Completed", "Deleted", "Returned"
    );

    public void validateTransition(Status from, Status to) {
        if (!validStatuses.contains(to.getName())) {
            throw new IllegalArgumentException("Invalid status: " + to.getName());
        }
        // Additional transition rules can be added here
    }
}