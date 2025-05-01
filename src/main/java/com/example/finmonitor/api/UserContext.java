package com.example.finmonitor.api;

import com.example.finmonitor.domain.model.User;
import com.example.finmonitor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserContext {

    @Autowired
    private UserRepository userRepository;

    /**
     * Возвращает ID текущего аутентифицированного пользователя.
     * Бросает RuntimeException, если пользователь не аутентифицирован или не найден.
     */
    public UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        String username = auth.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        return user.getId();
    }
}
