package com.main.docmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.main.docmanager.dto.LoginRequest;
import com.main.docmanager.dto.LoginResponse;
import com.main.docmanager.dto.RegisterRequest;
import com.main.docmanager.model.User;
import com.main.docmanager.repository.UserRepository;
import com.main.docmanager.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    @Autowired
	    private JwtUtil jwtUtil;
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private AuthenticationManager authenticationManager;

	    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	@Transactional
	public ResponseEntity<String> registerUser(RegisterRequest request) throws Exception {
		if (userRepository.findByUsername(request.username()).isPresent()) {
			logger.warn("Username already exists: {}", request.username());
			return ResponseEntity.badRequest().body("Username already exists");
		}
		String role = request.role().startsWith("ROLE_") ? request.role() : "ROLE_" + request.role();
		User user = new User(request.username(), passwordEncoder.encode(request.password()), role);

		try {
			userRepository.save(user);
			logger.info("User registered successfully: {}", request.username());
			return ResponseEntity.ok("User registered successfully");
		} catch (Exception e) {
			
			logger.error("Error pccured while registering user", e.getMessage());
			throw e;
		}
	}
	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        return new LoginResponse(jwt,request.username());
	}
	
}
