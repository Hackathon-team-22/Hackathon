package com.example.finmonitor.domain.service;

import com.example.finmonitor.domain.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {
    private TransactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TransactionValidator();
    }

    @Test
    void validateTIN_valid_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validateTIN("12345678901"));
    }

    @Test
    void validateTIN_invalidLength_throws() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateTIN("12345"));
    }

    @Test
    void validatePhone_validPlus7_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validatePhone("+71234567890"));
    }

    @Test
    void validatePhone_valid8_doesNotThrow() {
        assertDoesNotThrow(() -> validator.validatePhone("81234567890"));
    }

    @Test
    void validatePhone_invalidPrefix_throws() {
        assertThrows(IllegalArgumentException.class, () -> validator.validatePhone("91234567890"));
    }

    @Test
    void checkEditable_newStatus_doesNotThrow() {
        Status status = new Status();
        status.setName("New");
        assertDoesNotThrow(() -> validator.checkEditable(status));
    }

    @Test
    void checkEditable_nonEditableStatuses_throw() {
        String[] nonEditable = {"Confirmed","Processing","Cancelled","Completed","Deleted","Returned"};
        for (String name : nonEditable) {
            Status s = new Status(); s.setName(name);
            assertThrows(IllegalStateException.class, () -> validator.checkEditable(s), "Status " + name + " should not be editable");
        }
    }
}