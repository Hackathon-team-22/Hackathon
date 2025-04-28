package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusMachineTest {
    private StatusMachine machine;

    @BeforeEach
    void setUp() {
        machine = new StatusMachine();
    }

    @Test
    void validateTransition_validStatus_doesNotThrow() {
        Status to = new Status();
        to.setName("Processing");
        assertDoesNotThrow(() -> machine.validateTransition(null, to));
    }

    @Test
    void validateTransition_invalidStatus_throws() {
        Status to = new Status();
        to.setName("Unknown");
        assertThrows(IllegalArgumentException.class, () -> machine.validateTransition(null, to));
    }
}