package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ro.unibuc.hello.data.UserEntity;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
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
}