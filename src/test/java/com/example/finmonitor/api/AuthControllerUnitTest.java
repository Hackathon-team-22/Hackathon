package com.example.finmonitor.api;

import com.example.finmonitor.api.dto.RegisterRequest;
import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.domain.repository.UserRepository;
import com.example.finmonitor.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    void register_Success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("pass");

        given(userRepository.existsByUsername("user")).willReturn(false);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(req)))
                .andExpect(status().isCreated());

        verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void register_Conflict() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setPassword("pass");

        given(userRepository.existsByUsername("user")).willReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(req)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists"));
    }

    @Test
    void login_Success() throws Exception {
        given(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .willReturn(Mockito.mock(Authentication.class));
        given(jwtProvider.generateToken(org.mockito.ArgumentMatchers.any()))
                .willReturn("token");

        String body = "{\"username\":\"user\",\"password\":\"pass\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("token"));
    }

    @Test
    void refresh_Success() throws Exception {
        given(jwtProvider.refreshToken("refresh-token")).willReturn("new-token");

        String body = "{\"refreshToken\":\"refresh-token\"}";

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-token"));
    }

    @Test
    void refresh_Invalid() throws Exception {
        given(jwtProvider.refreshToken("bad-token"))
                .willThrow(new io.jsonwebtoken.JwtException("Invalid refresh token"));

        String body = "{\"refreshToken\":\"bad-token\"}";

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    private static String asJsonString(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
