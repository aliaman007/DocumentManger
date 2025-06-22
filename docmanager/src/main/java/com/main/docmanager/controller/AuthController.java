
package com.main.docmanager.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.main.docmanager.dto.LoginRequest;
import com.main.docmanager.dto.LoginResponse;
import com.main.docmanager.dto.RegisterRequest;
import com.main.docmanager.model.User;
import com.main.docmanager.repository.UserRepository;
import com.main.docmanager.security.JwtUtil;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        logger.debug("Registering user: {}", request.username());
        if (userRepository.findByUsername(request.username()).isPresent()) {
            logger.warn("Username already exists: {}", request.username());
            return ResponseEntity.badRequest().body("Username already exists");
        }
        String role = request.role().startsWith("ROLE_") ? request.role() : "ROLE_" + request.role();
        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                role
        );
        userRepository.save(user);
        logger.info("User registered successfully: {}", request.username());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.debug("Authenticating user: {}", request.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        logger.info("Login successful for user: {}", request.username());
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        logger.debug("User logout requested");
        return ResponseEntity.ok("Logout successful. Please discard the JWT token.");
    }
}

