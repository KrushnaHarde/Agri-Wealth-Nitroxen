package com.nitroxen.demo.service;

import com.nitroxen.demo.config.JwtService;
import com.nitroxen.demo.dto.request.ChangePasswordRequest;
import com.nitroxen.demo.dto.request.LoginRequest;
import com.nitroxen.demo.dto.response.AuthResponse;
import com.nitroxen.demo.entity.User;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );

        User user = userService.findByPhoneNumber(request.getPhoneNumber());
        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .userId(user.getId())
                .name(user.getName())
                .role(user.getRole().name())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime() / 1000) // Convert to seconds
                .build();
    }

    public void changePassword(ChangePasswordRequest request) {
        // Use the phone number from the request, not from the authentication context
        userService.changePassword(request.getPhoneNumber(), request.getCurrentPassword(), request.getNewPassword());
    }
}
