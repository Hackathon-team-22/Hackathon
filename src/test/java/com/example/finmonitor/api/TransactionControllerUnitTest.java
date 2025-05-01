package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.TransactionRequest;
import com.example.finmonitor.application.service.CreateTransactionService;
import com.example.finmonitor.application.service.DeleteTransactionService;
import com.example.finmonitor.application.service.FilterTransactionsService;
import com.example.finmonitor.application.service.GetTransactionService;
import com.example.finmonitor.application.service.UpdateTransactionService;
import com.example.finmonitor.domain.model.*;
import com.example.finmonitor.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CreateTransactionService createService;
    @MockBean private GetTransactionService getService;
    @MockBean private UpdateTransactionService updateService;
    @MockBean private DeleteTransactionService deleteService;
    @MockBean private FilterTransactionsService filterService;
    @MockBean private UserContext userContext;
    @MockBean private JwtProvider jwtProvider;

    private final UUID userId = UUID.fromString("11111111-2222-3333-4444-555555555555");
    private final UUID txId   = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");

    private static String asJson(Object o) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                    .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        given(userContext.getCurrentUserId()).willReturn(userId);
    }

    @Nested @DisplayName("POST /transactions")
    class Create {

        @Test @DisplayName("возвращает 201 и тело созданной транзакции")
        void create_returns201AndBody() throws Exception {
            var req = new TransactionRequest(
                    null,
                    LocalDateTime.of(2025,5,1,12,0),
                    UUID.fromString("11111111-0000-0000-0000-000000000000"),
                    UUID.fromString("22222222-0000-0000-0000-000000000000"),
                    UUID.fromString("33333333-0000-0000-0000-000000000000"),
                    UUID.fromString("44444444-0000-0000-0000-000000000000"),
                    UUID.fromString("55555555-0000-0000-0000-000000000000"),
                    "ACC1",
                    "ACC2",
                    UUID.fromString("66666666-0000-0000-0000-000000000000"),
                    new BigDecimal("1234.56"),
                    "12345678901",
                    "+71234567890",
                    "comment"
            );
            // domain entity returned by service
            var tx = new Transaction(
                    txId,
                    new User(userId, null, null, null),
                    LocalDateTime.of(2025,5,1,12,0),
                    new PartyType(req.getPartyTypeId(), null),
                    new TransactionType(req.getTransactionTypeId(), null),
                    new Status(req.getStatusId(), null),
                    new Bank(req.getBankSenderId(), null),
                    new Bank(req.getBankReceiverId(), null),
                    req.getAccountSender(),
                    req.getAccountReceiver(),
                    new Category(req.getCategoryId(), null),
                    req.getAmount(),
                    req.getReceiverTin(),
                    req.getReceiverPhone(),
                    req.getComment()
            );
            given(createService.execute(any(Transaction.class), eq(userId))).willReturn(tx);

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(txId.toString()))
                    .andExpect(jsonPath("$.createdByUserId").value(userId.toString()))
                    .andExpect(jsonPath("$.partyTypeId").value(req.getPartyTypeId().toString()))
                    .andExpect(jsonPath("$.amount").value(1234.56))
                    .andExpect(jsonPath("$.receiverTin").value("12345678901"));
        }
    }

    @Nested @DisplayName("GET /transactions/{id}")
    class GetById {

        @Test @DisplayName("возвращает 200 и тело транзакции")
        void getById_returns200AndBody() throws Exception {
            var tx = new Transaction(
                    txId,
                    new User(userId, null, null, null),
                    LocalDateTime.of(2025,5,1,12,0),
                    new PartyType(UUID.randomUUID(), null),
                    new TransactionType(UUID.randomUUID(), null),
                    new Status(UUID.randomUUID(), null),
                    new Bank(UUID.randomUUID(), null),
                    new Bank(UUID.randomUUID(), null),
                    "a","b", new Category(UUID.randomUUID(), null),
                    new BigDecimal("10.00"),
                    "12345678901","+71234567890","c"
            );
            given(getService.execute(eq(txId), eq(userId))).willReturn(tx);

            mockMvc.perform(get("/transactions/{id}", txId)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(txId.toString()))
                    .andExpect(jsonPath("$.amount").value(10.00))
                    .andExpect(jsonPath("$.comment").value("c"));
        }
    }

    @Nested @DisplayName("PUT /transactions/{id}")
    class Update {

        @Test @DisplayName("возвращает 200 и тело обновленной транзакции")
        void update_returns200AndBody() throws Exception {
            var req = new TransactionRequest(
                    null, LocalDateTime.now(),
                    UUID.randomUUID(), UUID.randomUUID(),
                    UUID.randomUUID(), UUID.randomUUID(),
                    UUID.randomUUID(), "x","y",
                    UUID.randomUUID(), BigDecimal.ONE,
                    "00000000000", "+70000000000", "upd"
            );
            var updated = new Transaction(
                    txId,
                    new User(userId,null,null,null),
                    req.getTimestamp(),
                    new PartyType(req.getPartyTypeId(),null),
                    new TransactionType(req.getTransactionTypeId(),null),
                    new Status(req.getStatusId(),null),
                    new Bank(req.getBankSenderId(),null),
                    new Bank(req.getBankReceiverId(),null),
                    req.getAccountSender(),req.getAccountReceiver(),
                    new Category(req.getCategoryId(),null),
                    req.getAmount(),req.getReceiverTin(),
                    req.getReceiverPhone(),req.getComment()
            );
            given(updateService.execute(eq(txId), any(Transaction.class), eq(userId))).willReturn(updated);

            mockMvc.perform(put("/transactions/{id}", txId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJson(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(txId.toString()))
                    .andExpect(jsonPath("$.comment").value("upd"));
        }
    }

    @Nested @DisplayName("DELETE /transactions/{id}")
    class Delete {

        @Test @DisplayName("возвращает 204 No Content")
        void delete_returns204() throws Exception {
            mockMvc.perform(delete("/transactions/{id}", txId))
                    .andExpect(status().isNoContent());
            // убедимся, что сервис вызван
            given(userContext.getCurrentUserId()).willReturn(userId);
            verify(deleteService).execute(eq(txId), eq(userId));
        }
    }

    @Nested @DisplayName("GET /transactions")
    class TransactionsList {

        @Test @DisplayName("возвращает страницу транзакций")
        void list_returnsPage() throws Exception {
            var tx = new Transaction(
                    txId,
                    new User(userId,null,null,null),
                    LocalDateTime.of(2025,5,1,12,0),
                    new PartyType(UUID.randomUUID(),null),
                    new TransactionType(UUID.randomUUID(),null),
                    new Status(UUID.randomUUID(),null),
                    new Bank(UUID.randomUUID(),null),
                    new Bank(UUID.randomUUID(),null),
                    "a","b", new Category(UUID.randomUUID(),null),
                    new BigDecimal("5.55"),"12345678901","+79991234567","f"
            );
            Page<Transaction> page = new PageImpl<>(List.of(tx), PageRequest.of(0,10), 1);
            given(filterService.execute(
                    any(), any(), any(), any(),
                    any(), any(), any(), any(),
                    any(), any(), eq(userId), any(Pageable.class)
            ))
                    .willReturn(page);

            mockMvc.perform(get("/transactions")
                            .param("page", "0")
                            .param("size", "10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(txId.toString()))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.size").value(10));
        }
    }
}
