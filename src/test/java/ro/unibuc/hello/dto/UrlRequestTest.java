package ro.unibuc.hello.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class UrlRequestTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String originalUrl = "https://example.com";
        LocalDateTime expiresAt = LocalDateTime.of(2023, 12, 31, 23, 59, 59);

        // Act
        UrlRequest urlRequest = new UrlRequest(originalUrl, expiresAt);

        // Assert
        assertEquals(originalUrl, urlRequest.getOriginalUrl());
        assertEquals(expiresAt, urlRequest.getExpiresAt());
    }

    @Test
    void testSetters() {
        // Arrange
        UrlRequest urlRequest = new UrlRequest("https://example.com",
                LocalDateTime.of(2023, 12, 31, 23, 59, 59));

        String newUrl = "https://newexample.com";
        LocalDateTime newExpiresAt = LocalDateTime.of(2024, 12, 31, 23, 59, 59);

        // Act
        urlRequest.setOriginalUrl(newUrl);
        urlRequest.setExpiresAt(newExpiresAt);

        // Assert
        assertEquals(newUrl, urlRequest.getOriginalUrl());
        assertEquals(newExpiresAt, urlRequest.getExpiresAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        LocalDateTime expiresAt = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        UrlRequest urlRequest1 = new UrlRequest("https://example.com", expiresAt);
        UrlRequest urlRequest2 = new UrlRequest("https://example.com", expiresAt);
        UrlRequest urlRequest3 = new UrlRequest("https://different.com", expiresAt);

        // Assert
        assertEquals(urlRequest1, urlRequest2);
        assertEquals(urlRequest1.hashCode(), urlRequest2.hashCode());

        assertNotEquals(urlRequest1, urlRequest3);
        assertNotEquals(urlRequest1.hashCode(), urlRequest3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        String originalUrl = "https://example.com";
        LocalDateTime expiresAt = LocalDateTime.of(2023, 12, 31, 23, 59, 59);
        UrlRequest urlRequest = new UrlRequest(originalUrl, expiresAt);

        // Act
        String toString = urlRequest.toString();

        // Assert
        assertTrue(toString.contains(originalUrl));
        assertTrue(toString.contains(expiresAt.toString()));
    }
}