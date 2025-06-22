package com.main.docmanager.dto;

public record LoginRequest(
        String username,
        String password
) {}