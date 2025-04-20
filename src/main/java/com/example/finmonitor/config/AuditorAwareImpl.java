package com.example.finmonitor.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<UUID> {
    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            // У вас должен быть UserDetails с методом getId(), кастуйте по необходимости
            // Для простоты предположим, что имя пользователя - это UUID
            try {
                return Optional.of(UUID.fromString(auth.getName()));
            } catch (IllegalArgumentException e) {
                // имя не UUID
            }
        }
        return Optional.empty();
    }
}