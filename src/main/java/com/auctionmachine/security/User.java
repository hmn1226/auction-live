package com.auctionmachine.security;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "users") // ← "user" を避ける
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    // パスワードはBCryptなどでハッシュ化して保存しましょう
    private String password;

    // コンストラクタ、ゲッター・セッターなど
}