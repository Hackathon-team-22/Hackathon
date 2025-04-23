//// src/test/java/com/example/finmonitor/api/TransactionControllerIntegrationTest.java
//package com.example.finmonitor.api;
//
//import com.example.finmonitor.domain.model.*;
//import com.example.finmonitor.domain.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Testcontainers
//class TransactionControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired private UserRepository userRepository;
//    @Autowired private BankRepository bankRepository;
//    @Autowired private PartyTypeRepository partyTypeRepository;
//    @Autowired private TransactionTypeRepository transactionTypeRepository;
//    @Autowired private StatusRepository statusRepository;
//    @Autowired private CategoryRepository categoryRepository;
//    @Autowired private PasswordEncoder passwordEncoder;
//    @Autowired private TransactionRepository transactionRepository;
//
//    @Container
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
//            .withDatabaseName("fin_monitoring")
//            .withUsername("user")
//            .withPassword("password");
//
//    @DynamicPropertySource
//    static void overrideProps(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url",     postgres::getJdbcUrl);
//        registry.add("spring.datasource.username",postgres::getUsername);
//        registry.add("spring.datasource.password",postgres::getPassword);
//    }
//
//    private String token;
//    private UUID createdByUserId;
//    private UUID partyTypeId, transactionTypeId, statusId, bankSenderId, bankReceiverId, categoryId;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // Очистка данных
//        transactionRepository.deleteAll();
//        userRepository.deleteAll();
//        bankRepository.deleteAll();
//        partyTypeRepository.deleteAll();
//        transactionTypeRepository.deleteAll();
//        statusRepository.deleteAll();
//        categoryRepository.deleteAll();
//
//        // Создать пользователя
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPasswordHash(passwordEncoder.encode("secret"));
//        user.setRole("ROLE_USER");
//        userRepository.save(user);
//        createdByUserId = user.getId();
//
//        // Создать справочники
//        partyTypeId       = partyTypeRepository.save(new PartyType(null, "Physical")).getId();
//        transactionTypeId = transactionTypeRepository.save(new TransactionType(null, "Credit")).getId();
//        statusId          = statusRepository.save(new Status(null, "New")).getId();
//        bankSenderId      = bankRepository.save(new Bank(null, "SenderBank")).getId();
//        bankReceiverId    = bankRepository.save(new Bank(null, "ReceiverBank")).getId();
//        categoryId        = categoryRepository.save(new Category(null, "General")).getId();
//
//        // Получить JWT
//        String loginJson = String.format("{\"username\":\"%s\",\"password\":\"secret\"}", user.getUsername());
//        String resp = mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(loginJson))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//        token = resp.replaceAll(".*\\\"accessToken\\\":\\\"(.*?)\\\".*", "$1");
//    }
//
//    @Test
//    void create_and_getTransaction_flow() throws Exception {
//        // Текущий timestamp для транзакции
//        String now = LocalDateTime.now().toString();
//
//        String txJson = String.format(
//                "{"
//                        + "\"createdByUser\":{\"id\":\"%s\"},"
//                        + "\"timestamp\":\"%s\","
//                        + "\"partyType\":{\"id\":\"%s\"},"
//                        + "\"transactionType\":{\"id\":\"%s\"},"
//                        + "\"status\":{\"id\":\"%s\"},"
//                        + "\"bankSender\":{\"id\":\"%s\"},"
//                        + "\"bankReceiver\":{\"id\":\"%s\"},"
//                        + "\"accountSender\":\"1234567890\","
//                        + "\"accountReceiver\":\"0987654321\","
//                        + "\"category\":{\"id\":\"%s\"},"
//                        + "\"amount\":100.50,"
//                        + "\"receiverTin\":\"12345678901\","
//                        + "\"receiverPhone\":\"81234567890\","
//                        + "\"comment\":\"Test transaction\""
//                        + "}",
//                createdByUserId,
//                now,
//                partyTypeId,
//                transactionTypeId,
//                statusId,
//                bankSenderId,
//                bankReceiverId,
//                categoryId
//        );
//
//        // Создание транзакции
//        mockMvc.perform(post("/transactions")
//                        .header("Authorization", "Bearer " + token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(txJson))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").isNotEmpty());
//
//        // Просмотр списка транзакций
//        mockMvc.perform(get("/transactions?page=0&size=10")
//                        .header("Authorization", "Bearer " + token))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray());
//    }
//}
