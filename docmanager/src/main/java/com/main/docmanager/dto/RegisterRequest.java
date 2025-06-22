package com.main.docmanager.dto;

public record RegisterRequest(
        String username,
        String password,
        String role
) {}