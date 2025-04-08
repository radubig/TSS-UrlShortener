package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.exception.EntityNotFoundException;
import ro.unibuc.hello.exception.NoPermissionException;
import ro.unibuc.hello.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserEntity testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        // Set up test user
        testUser = new UserEntity();
        testUser.setId("user123");
        testUser.setUsername("testuser");

        // Configure security context mock
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testuser");

        // Configure userService mock
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        // Arrange
        User registerRequest = new User();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");

        UserEntity createdUser = new UserEntity();
        createdUser.setUsername("newuser");
        createdUser.setEmail("newuser@example.com");

        when(userService.registerUser(any(User.class))).thenReturn(createdUser);

        // Act & Assert
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testRegisterUserFailure() throws Exception {
        // Arrange
        User registerRequest = new User();
        registerRequest.setUsername("existinguser");
        registerRequest.setEmail("existing@example.com");
        registerRequest.setPassword("password123");

        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());

        verify(userService).registerUser(any(User.class));
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        // Arrange
        String username = "userToDelete";
        String currentUserId = "user123";

        doNothing().when(userService).deleteUser(username, currentUserId);

        // Act & Assert
        mockMvc.perform(delete("/api/user/delete/" + username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));

        verify(userService).deleteUser(username, currentUserId);
    }

    @Test
    public void testDeleteUserFailure() throws Exception {
        // Arrange
        String username = "userToDelete";
        String currentUserId = "user123";

        doThrow(new NoPermissionException("You are not allowed to delete this user")).when(userService).deleteUser(username, currentUserId);

        // Act & Assert
        mockMvc.perform(delete("/api/user/delete/" + username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService).deleteUser(username, currentUserId);
    }
}