package com.auctionmachine.resources.service;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auctionmachine.security.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
