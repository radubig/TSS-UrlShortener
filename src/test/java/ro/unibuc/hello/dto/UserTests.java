package ro.unibuc.hello.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTests {

    @Test
    void testAllArgsConstructorAndGetters() {
        // Arrange & Act
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        User user = new User(username, email, password);

        // Assert
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        // Arrange
        User user = new User();

        // Act
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        // Assert
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        User user1 = new User("testuser", "test@example.com", "password123");
        User user2 = new User("testuser", "test@example.com", "password123");
        User user3 = new User("otheruser", "test@example.com", "password123");

        // Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password123");

        // Act
        String toString = user.toString();

        // Assert
        assertTrue(toString.contains("username=testuser"));
        assertTrue(toString.contains("email=test@example.com"));
        assertTrue(toString.contains("password=password123"));
    }
}