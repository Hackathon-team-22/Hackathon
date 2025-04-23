package com.example.finmonitor.api;

import com.example.finmonitor.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIntegrationTest {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password";

    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TestReferenceDataHelper testHelper;
    @Autowired private UserRepository userRepository;

    private UUID partyTypeId;
    private UUID typeId;
    private UUID statusId;
    private UUID bankId;
    private UUID categoryId;

    @BeforeEach
    void setupTestData() {
        userRepository.findOptionalByUsername(USERNAME)
                .ifPresent(userRepository::delete);

        testHelper.getOrCreateUser(USERNAME, passwordEncoder.encode(PASSWORD), "USER");
        partyTypeId = testHelper.getOrCreatePartyType("Физическое лицо");
        typeId = testHelper.getOrCreateTransactionType("Поступление");
        statusId = testHelper.getOrCreateStatus("Новая");
        bankId = testHelper.getOrCreateBank("ТестБанк");
        categoryId = testHelper.getOrCreateCategory("Образование");
    }

    private String obtainAccessToken(String username, String password) throws Exception {
        String loginRequest = """
            {
              "username": "%s",
              "password": "%s"
            }
            """.formatted(username, password);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(result.getResponse().getContentAsString());
        return json.get("accessToken").asText();
    }

    @Test
    void createTransaction_shouldReturn201() throws Exception {
        String token = obtainAccessToken(USERNAME, PASSWORD);

        String requestJson = """
            {
              "timestamp": "2025-04-23T12:00:00",
              "partyTypeId": "%s",
              "transactionTypeId": "%s",
              "statusId": "%s",
              "bankSenderId": "%s",
              "bankReceiverId": "%s",
              "accountSender": "40817810000000000001",
              "accountReceiver": "40817810000000000002",
              "categoryId": "%s",
              "amount": 100.0,
              "receiverTin": "12345678901",
              "receiverPhone": "+71234567890",
              "comment": "Тест"
            }
            """.formatted(partyTypeId, typeId, statusId, bankId, bankId, categoryId);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void deleteTransaction_withConfirmedStatus_shouldReturn409() throws Exception {
        String token = obtainAccessToken(USERNAME, PASSWORD);
        UUID confirmedStatusId = testHelper.getOrCreateStatus("Подтверждена");
        UUID txId = testHelper.createTransaction(partyTypeId, typeId, confirmedStatusId, bankId, bankId, categoryId, USERNAME);

        mockMvc.perform(delete("/transactions/" + txId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    void updateTransaction_withEditableStatus_shouldReturn200() throws Exception {
        String token = obtainAccessToken(USERNAME, PASSWORD);
        UUID txId = testHelper.createTransaction(partyTypeId, typeId, statusId, bankId, bankId, categoryId, USERNAME);

        String updateJson = """
            {
              "comment": "Обновлённый комментарий"
            }
            """;

        mockMvc.perform(put("/transactions/" + txId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactions_shouldReturn200() throws Exception {
        String token = obtainAccessToken(USERNAME, PASSWORD);
        testHelper.createTransaction(partyTypeId, typeId, statusId, bankId, bankId, categoryId, USERNAME);

        mockMvc.perform(get("/transactions")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
