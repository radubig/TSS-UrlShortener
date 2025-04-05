package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ro.unibuc.hello.dto.UrlRequest;
import ro.unibuc.hello.dto.UrlStats;
import ro.unibuc.hello.service.UrlShortenerService;
import ro.unibuc.hello.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UrlControllerTest {

    @Mock
    private UrlShortenerService urlShortenerService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UrlController urlController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserEntity testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();

        // Configure ObjectMapper to handle Java 8 Date/Time types
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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
    public void testCreateShortUrl() throws Exception {
        // Arrange
        UrlRequest urlRequest = new UrlRequest();
        urlRequest.setOriginalUrl("https://example.com");
        urlRequest.setExpiresAt(LocalDateTime.now().plusDays(7));

        when(urlShortenerService.createShortUrl(any(UrlRequest.class), eq("user123")))
            .thenReturn("abc123");

        // Act & Assert
        mockMvc.perform(post("/api/urls/createShortUrl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("abc123"));

        verify(urlShortenerService).createShortUrl(any(UrlRequest.class), eq("user123"));
    }

    @Test
    public void testGetOriginalUrl() throws Exception {
        // Arrange
        String shortUrl = "abc123";
        String originalUrl = "https://example.com";

        when(urlShortenerService.getOriginalUrl(shortUrl)).thenReturn(originalUrl);

        // Act & Assert
        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(originalUrl));

        verify(urlShortenerService).getOriginalUrl(shortUrl);
    }

    @Test
    public void testDeleteShortUrl() throws Exception {
        // Arrange
        String shortUrl = "abc123";

        doNothing().when(urlShortenerService).deleteShortUrl(shortUrl, "user123");

        // Act & Assert
        mockMvc.perform(delete("/api/urls/delete/{shortUrl}", shortUrl))
                .andExpect(status().isOk())
                .andExpect(content().string("Url deleted successfully"));

        verify(urlShortenerService).deleteShortUrl(shortUrl, "user123");
    }

    @Test
    public void testGetStats() throws Exception {
        // Arrange
        String shortUrl = "abc123";
        UrlStats urlStats = UrlStats.builder()
            .shortUrl(shortUrl)
            .totalVisits(10L)
            .build();

        when(urlShortenerService.getUrlStats(shortUrl, "user123")).thenReturn(urlStats);

        // Act & Assert
        mockMvc.perform(get("/api/urls/getStats/{shortUrl}", shortUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(shortUrl))
                .andExpect(jsonPath("$.totalVisits").value(10));

        verify(urlShortenerService).getUrlStats(shortUrl, "user123");
    }
}