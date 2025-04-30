package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.TransactionRequest;
import com.example.finmonitor.application.service.*;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerUnitTest {

    private MockMvc mockMvc;

    @Mock private CreateTransactionService createService;
    @Mock private GetTransactionService getService;
    @Mock private UpdateTransactionService updateService;
    @Mock private DeleteTransactionService deleteService;
    @Mock private FilterTransactionsService filterService;
    @Mock private UserRepository userRepository;
    @Mock private UserContext userContext;

    @InjectMocks
    private TransactionController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void createSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        given(userContext.getCurrentUserId()).willReturn(userId);

        User user = new User(); user.setId(userId);
        given(userRepository.findByUsername("user")).willReturn(user);

        given(createService.execute(any(Transaction.class), eq(userId)))
                .willReturn(new Transaction() {{ setId(UUID.randomUUID()); }});

        TransactionRequest req = new TransactionRequest();
        req.setTimestamp(LocalDateTime.now());
        // ... заполняем остальные поля ...

        mockMvc.perform(post("/transactions")
                        .with(user("user"))
                        .contentType("application/json")
                        .content(asJsonString(req)))
                .andExpect(status().isCreated());

        verify(createService).execute(any(Transaction.class), eq(userId));
    }

    @Test
    void deleteSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        given(userContext.getCurrentUserId()).willReturn(userId);

        UUID txId = UUID.randomUUID();
        mockMvc.perform(delete("/transactions/{id}", txId)
                        .with(user("user")))
                .andExpect(status().isNoContent());

        verify(deleteService).execute(txId, userId);
    }

    @Test
    void getByIdSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        given(userContext.getCurrentUserId()).willReturn(userId);

        UUID txId = UUID.randomUUID();
        given(getService.execute(txId, userId))
                .willReturn(new Transaction() {{ setId(txId); }});

        mockMvc.perform(get("/transactions/{id}", txId)
                        .with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(txId.toString()));

        verify(getService).execute(txId, userId);
    }

    // ... аналогично остальные тесты ...

    private static String asJsonString(Object obj) {
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
