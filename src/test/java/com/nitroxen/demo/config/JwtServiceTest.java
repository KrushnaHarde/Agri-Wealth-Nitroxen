package com.nitroxen.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private final String testSecretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long testExpiration = 3600000; // 1 hour
    private final String username = "+1234567890"; // Phone number as username

    @BeforeEach
    void setUp() {
        // Setup mocked UserDetails
        when(userDetails.getUsername()).thenReturn(username);

        // Inject our test values using reflection
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    void generateToken_ReturnsValidJwtToken() {
        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertThat(token).isNotBlank();
        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void generateTokenWithClaims_ReturnsValidJwtToken() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "OWNER");

        // Act
        String token = jwtService.generateToken(extraClaims, userDetails);

        // Assert
        assertThat(token).isNotBlank();
        String extractedUsername = jwtService.extractUsername(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void isTokenValid_ExpiredToken_ReturnsFalse() throws Exception {
        // Create a token that is already expired
        // This requires custom creation of an expired token

        // First, use reflection to temporarily set a negative expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -10000); // Expired 10 seconds ago

        // Generate token with negative expiration
        String expiredToken = jwtService.generateToken(userDetails);

        // Reset expiration to original value
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);

        // Act
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void isTokenValid_WrongUsername_ReturnsFalse() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Create a mock UserDetails with different username
        UserDetails differentUser = org.mockito.Mockito.mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("different-user");

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void extractUsername_ValidToken_ReturnsCorrectUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void getExpirationTime_ReturnsConfiguredValue() {
        // Act
        long expirationTime = jwtService.getExpirationTime();

        // Assert
        assertThat(expirationTime).isEqualTo(testExpiration);
    }
}
