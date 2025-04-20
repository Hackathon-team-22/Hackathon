package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.Status;
import com.example.finmonitor.domain.repository.StatusRepository;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

@Component
public class TransactionValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Set<String> nonEditableStatuses = Set.of(
            "Confirmed", "Processing", "Cancelled", "Completed", "Deleted", "Returned"
    );

    @Autowired
    private StatusRepository statusRepository;

    public void validateDate(String date) {
        try {
            DATE_FORMATTER.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Date must be in format dd.MM.yyyy");
        }
    }

    public void validateTIN(String tin) {
        if (!tin.matches("\\d{11}")) {
            throw new IllegalArgumentException("TIN must be exactly 11 digits");
        }
    }

    public void validatePhone(String phone) {
        if (!phone.matches("(\\+7|8)\\d{10}")) {
            throw new IllegalArgumentException("Phone must match +7XXXXXXXXXX or 8XXXXXXXXXX");
        }
    }

    public void checkEditable(Status status) {
        if (nonEditableStatuses.contains(status.getName())) {
            throw new IllegalStateException(
                    "Transaction with status '" + status.getName() + "' cannot be edited"
            );
        }
    }
}