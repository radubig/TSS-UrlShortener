package ro.unibuc.hello.controller;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ro.unibuc.hello.data.ShortUrlEntity;
import ro.unibuc.hello.data.ShortUrlRepository;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.UrlRequest;
import ro.unibuc.hello.dto.User;
import ro.unibuc.hello.service.UrlShortenerService;
import ro.unibuc.hello.service.UserService;
import ro.unibuc.hello.utils.ShortUrlGenerator;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class UrlControllerIntegrationTest {
    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withExposedPorts(27017)
            .withSharding();


    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        final String MONGO_URL = "mongodb://localhost:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));

        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private UrlShortenerService urlShortenerService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthController authController;
    @Autowired
    private ShortUrlRepository shortUrlRepository;
    @MockitoBean
    private ShortUrlGenerator shortUrlGenerator;

    @BeforeAll
    public static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    private final User testUser = new User("testUser", "testuser@email.com", "123testPassword");

    @BeforeEach
    public void setUpTests(){
        userRepository.deleteAll();
        userService.registerUser(testUser);
        MockitoAnnotations.openMocks(this);
    }

    private static final String BASE_URL = "/api/urls";

    @Test
    public void testCreateShortUrl() throws Exception{
        UrlRequest newUrlRequest = new UrlRequest("www.google.com", null);
        String token = ((Map<String,String>) authController.login(testUser).getBody()).get("token");
        String expectedShortUrl = "Jdh72z";
        when(shortUrlGenerator.getShortUrl()).thenReturn(expectedShortUrl);
        mockMvc.perform(post(BASE_URL + "/createShortUrl")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(newUrlRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedShortUrl));
    }

    @Test
    public void testGetUrlStats() throws Exception{
        ShortUrlEntity shortUrlEntity = new ShortUrlEntity();
        shortUrlEntity.setOriginalUrl("www.google.com");
        shortUrlEntity.setTotalVisits(5L);
        String shortUrl = "Osm27r";
        shortUrlEntity.setShortenedUrl(shortUrl);
        shortUrlEntity.setCreatorUserId(userService.getUserByUsername(testUser.getUsername()).getId());

        shortUrlRepository.save(shortUrlEntity);

        String token = ((Map<String,String>) authController.login(testUser).getBody()).get("token");

        mockMvc.perform(get(BASE_URL + "/getStats/" + shortUrl)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(shortUrl))
                .andExpect(jsonPath("$.totalVisits").value(5));

    }

}
