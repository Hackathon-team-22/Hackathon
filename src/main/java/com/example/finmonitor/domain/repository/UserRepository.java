package com.example.finmonitor.domain.repository;

import com.example.finmonitor.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);

    Optional<User> findOptionalByUsername(String username); // <-- добавить

    boolean existsByUsername(String username);
}
