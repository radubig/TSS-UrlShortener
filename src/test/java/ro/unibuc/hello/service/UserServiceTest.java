package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.exception.UserAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUserSuccess() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setUsername("testuser");
        savedEntity.setEmail("test@example.com");
        savedEntity.setPassword("encodedPassword");

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // Act
        UserEntity result = userService.registerUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUserUsernameExists() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("existinguser");
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(newUser));

        assertEquals("User with the same username already exists", exception.getMessage());

        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterUserEmailExists() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("existing@example.com");
        newUser.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(newUser));

        assertEquals("User with the same email already exists", exception.getMessage());

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testGetUserByUsername() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(userEntity);

        // Act
        UserEntity result = userService.getUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        // Act
        UserEntity result = userService.getUserByUsername("nonexistentuser");

        // Assert
        assertNull(result);
        verify(userRepository).findByUsername("nonexistentuser");
    }
}