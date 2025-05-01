package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.AuthRequest;
import com.example.finmonitor.api.dto.RegisterRequest;
import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.repository.UserRepository;
import com.example.finmonitor.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserRepository userRepository;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private JwtProvider jwtProvider;
    @MockBean private PasswordEncoder passwordEncoder;

    private static final String USERNAME = "user";
    private static final String PASSWORD = "pass";
    private static final String HASHED   = "hashedPass";

    private static String asJsonString(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                    .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("POST /auth/register")
    class RegisterTests {

        @Test
        @DisplayName("успешная регистрация → 201 + User сохранён с хэшем")
        void register_Success() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setUsername(USERNAME);
            req.setPassword(PASSWORD);

            given(userRepository.existsByUsername(USERNAME)).willReturn(false);
            given(passwordEncoder.encode(PASSWORD)).willReturn(HASHED);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.message").value("User registered successfully"));

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertThat(saved.getUsername()).isEqualTo(USERNAME);
            assertThat(saved.getPasswordHash()).isEqualTo(HASHED);
            assertThat(saved.getRole()).isEqualTo("USER");
        }

        @Test
        @DisplayName("пользователь уже существует → 409 + поле error")
        void register_UserExists_Conflict() throws Exception {
            RegisterRequest req = new RegisterRequest();
            req.setUsername(USERNAME);
            req.setPassword(PASSWORD);

            given(userRepository.existsByUsername(USERNAME)).willReturn(true);

            mockMvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(req)))
                    .andExpect(status().isConflict())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value("User already exists"));

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("успешная аутентификация → 200 + accessToken")
        void login_Success() throws Exception {
            AuthRequest req = new AuthRequest();
            req.setUsername(USERNAME);
            req.setPassword(PASSWORD);

            Authentication authMock = org.mockito.Mockito.mock(Authentication.class);
            given(authenticationManager.authenticate(any())).willReturn(authMock);
            given(jwtProvider.generateToken(authMock)).willReturn("token-123");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(req)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accessToken").value("token-123"));
        }

        @Test
        @DisplayName("неверные учетные данные → 401 + поле error")
        void login_InvalidCredentials_Unauthorized() throws Exception {
            AuthRequest req = new AuthRequest();
            req.setUsername(USERNAME);
            req.setPassword(PASSWORD);

            given(authenticationManager.authenticate(any()))
                    .willThrow(new BadCredentialsException("bad credentials"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error").value("Invalid username or password"));
        }
    }
}
