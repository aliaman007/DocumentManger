
package com.main.docmanager.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.docmanager.dto.LoginRequest;
import com.main.docmanager.dto.LoginResponse;
import com.main.docmanager.dto.RegisterRequest;
import com.main.docmanager.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authservice;
 

    @Operation(summary = "Register user here")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) throws Exception {
        logger.info("Registering user: {}", request.username());
        return authservice.registerUser(request);
    }

    @Operation(summary = "Authenticate user and generate JWT", description = "Authenticates a user with username and password, returning a JWT token.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content
        )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login successful for user: {}", request.username());
        return ResponseEntity.ok(authservice.login(request));
    }

    @Operation(summary = "Logout and flush JWT token")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        logger.debug("User logout requested");
        return ResponseEntity.ok("Logout successful. Please discard the JWT token.");
    }
}

