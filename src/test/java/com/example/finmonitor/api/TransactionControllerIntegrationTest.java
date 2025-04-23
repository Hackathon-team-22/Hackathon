// src/test/java/com/example/finmonitor/api/TransactionControllerIntegrationTest.java

package com.example.finmonitor.api;

import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.domain.repository.TransactionRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIntegrationTest {

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestReferenceDataHelper testHelper;

    @Autowired
    private UserRepository userRepository;

    private String partyTypeId;
    private String typeId;
    private String statusId;
    private String bankId;
    private String categoryId;

    @BeforeEach
    void setupTestData() throws Exception {
        // Удалить, если существует
        var existing = userRepository.findByUsername(USERNAME);
        if (existing != null) userRepository.delete(existing);

        // Создать нового пользователя
        User user = new User();
        user.setUsername(USERNAME);
        user.setPasswordHash(passwordEncoder.encode(PASSWORD));
        user.setRole("USER");
        userRepository.save(user);

        // Справочные данные
        partyTypeId = testHelper.ensurePartyType("Физическое лицо");
        typeId = testHelper.ensureTransactionType("Поступление");
        statusId = testHelper.ensureStatus("Новая");
        bankId = testHelper.ensureBank("ТестБанк");
        categoryId = testHelper.ensureCategory("Образование");
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

        String response = result.getResponse().getContentAsString();
        return response.replaceAll(".*\"accessToken\"\s*:\s*\"(.*?)\".*", "$1");
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
    void getTransactions_shouldReturn200() throws Exception {
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
                  "comment": "Для фильтрации"
                }
                """.formatted(partyTypeId, typeId, statusId, bankId, bankId, categoryId);

        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/transactions")
                        .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void deleteTransaction_withForbiddenStatus_shouldReturn409() throws Exception {
        String token = obtainAccessToken(USERNAME, PASSWORD);
        String id = "<existing_transaction_id_with_confirmed_status>";

        mockMvc.perform(delete("/transactions/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict());
    }

    @Test
    void updateTransaction_withEditableStatus_shouldReturn200() throws Exception {
        String token = obtainAccessToken(USERNAME, PASSWORD);
        String id = "<existing_transaction_id_with_editable_status>";

        String updateJson = """
                {
                  "comment": "Обновлённый комментарий"
                }
                """;

        mockMvc.perform(put("/transactions/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }
}
