// File: src/test/java/com/example/finmonitor/api/TransactionControllerUnitTest.java
package com.example.finmonitor.api;

import com.example.finmonitor.application.dto.TransactionRequest;
import com.example.finmonitor.domain.model.Transaction;
import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.application.service.CreateTransactionService;
import com.example.finmonitor.application.service.GetTransactionService;
import com.example.finmonitor.application.service.UpdateTransactionService;
import com.example.finmonitor.application.service.DeleteTransactionService;
import com.example.finmonitor.application.service.FilterTransactionsService;
import com.example.finmonitor.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "user")
public class TransactionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private com.example.finmonitor.security.JwtFilter jwtFilter;

    @MockitoBean
    private com.example.finmonitor.security.JwtProvider jwtProvider;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @MockitoBean
    private CreateTransactionService createService;

    @MockitoBean
    private GetTransactionService getService;

    @MockitoBean
    private UpdateTransactionService updateService;

    @MockitoBean
    private DeleteTransactionService deleteService;

    @MockitoBean
    private FilterTransactionsService filterService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void createSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        given(userRepository.findByUsername("user")).willReturn(user);
        given(createService.execute(any(Transaction.class), eq(userId)))
                .willReturn(new Transaction() {{ setId(UUID.randomUUID()); }});

        TransactionRequest req = new TransactionRequest();
        req.setTimestamp(LocalDateTime.now());
        req.setPartyTypeId(UUID.randomUUID());
        req.setTransactionTypeId(UUID.randomUUID());
        req.setStatusId(UUID.randomUUID());
        req.setBankSenderId(UUID.randomUUID());
        req.setBankReceiverId(UUID.randomUUID());
        req.setAccountSender("ACC123");
        req.setAccountReceiver("ACC456");
        req.setCategoryId(UUID.randomUUID());
        req.setAmount(BigDecimal.TEN);
        req.setReceiverTin("81234567890");
        req.setReceiverPhone("+71234567890");
        req.setComment("Test transaction");

        mockMvc.perform(post("/transactions")
                        .with(user("user"))
                        .contentType("application/json")
                        .content(asJsonString(req)))
                .andExpect(status().isCreated());

        verify(createService).execute(any(Transaction.class), eq(userId));
    }

    @Test
    void createUserNotFound() throws Exception {
        given(userRepository.findByUsername("user")).willReturn(null);

        TransactionRequest req = new TransactionRequest();
        req.setAmount(BigDecimal.ONE);

        mockMvc.perform(post("/transactions")
                        .contentType("application/json")
                        .content(asJsonString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/transactions/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteService).execute(id);
    }

    @Test
    void getByIdSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        given(getService.execute(id)).willReturn(new Transaction() {{ setId(id); }});

        mockMvc.perform(get("/transactions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));

        verify(getService).execute(id);
    }

    @Test
    void listSuccess() throws Exception {
        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk());
    }

    private static String asJsonString(Object obj) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
