package ro.unibuc.hello.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.data.ShortUrlEntity;
import ro.unibuc.hello.data.ShortUrlRepository;
import ro.unibuc.hello.dto.UrlRequest;
import ro.unibuc.hello.exception.NoPermissionException;
import ro.unibuc.hello.exception.ShortUrlNotFoundException;
import ro.unibuc.hello.utils.ShortUrlGenerator;
import ro.unibuc.hello.utils.Tracking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class UrlShortenerServiceTest {
    @Mock
    private ShortUrlRepository shortUrlRepository;
    @MockitoBean
    private ShortUrlGenerator shortUrlGenerator;
    @Mock
    private Tracking tracking;
    @InjectMocks
    private UrlShortenerService urlShortenerService;
    private ShortUrlEntity mockShortUrl = new ShortUrlEntity();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockShortUrl.setId("abc");
        mockShortUrl.setShortenedUrl("K8sjNf");
        mockShortUrl.setOriginalUrl("www.google.com");
        mockShortUrl.setCreatorUserId("abc");
        mockShortUrl.setTotalVisits(0L);
        mockShortUrl.setExpirationDate(LocalDateTime.now().plusMonths(1));
    }

    @Test
    void testRequestExistingUrl() {
        String originalUrl = "www.google.com";
        UrlRequest urlRequest = new UrlRequest(originalUrl, null);
        when(shortUrlRepository.findByOriginalUrl(originalUrl)).thenReturn(mockShortUrl);

        String shortUrl = urlShortenerService.createShortUrl(urlRequest, "abc");

        assertEquals(shortUrl, mockShortUrl.getShortenedUrl());
    }

    @Test
    void testRequestUrlInThePast() {
        String originalUrl = "www.test.com";
        UrlRequest urlRequest = new UrlRequest(originalUrl, LocalDateTime.now().minusSeconds(5));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, "abc"));
        assertEquals("Expiration date must be in the future", exception.getMessage());
    }

    @Test
    void testRequestNullUrl() {
        UrlRequest urlRequest = new UrlRequest(null, LocalDateTime.now().plusDays(16));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, "abc"));
        assertEquals("Url must not be empty", exception.getMessage());
    }

    @Test
    void testVisitShortenedUrl() {
        when(shortUrlRepository.findByShortenedUrl(mockShortUrl.getShortenedUrl()))
                .thenReturn(mockShortUrl);

        doNothing().when(tracking).incrementVisits(mockShortUrl.getShortenedUrl());
        String originalUrl = urlShortenerService.getOriginalUrl(mockShortUrl.getShortenedUrl());

        assertEquals(mockShortUrl.getOriginalUrl(), originalUrl);
    }

    @Test
    void testTryToDeleteWithWrongId() {
        when(shortUrlRepository.findByShortenedUrl(mockShortUrl.getShortenedUrl()))
                .thenReturn(mockShortUrl);
        NoPermissionException exception = assertThrows(NoPermissionException.class, () ->
                urlShortenerService.deleteShortUrl(mockShortUrl.getShortenedUrl(), "cba"));

        assertNotNull(exception);
        assertEquals("You are not allowed to delete this URL", exception.getMessage());
    }

    @Test
    void testTryToDeleteNonExistentUrl(){
        String shortUrl = "testUrl";
        when(shortUrlRepository.findByShortenedUrl(shortUrl))
                .thenReturn(null);
        ShortUrlNotFoundException exception = assertThrows(ShortUrlNotFoundException.class, () ->
                urlShortenerService.deleteShortUrl(shortUrl, "abc"));

        assertNotNull(exception);
        assertEquals("The shortened Url " + shortUrl + " was not found", exception.getMessage());
    }

    @Test
    void testTryToGetUrlStatsWithNoPermission(){
        when(shortUrlRepository.findByShortenedUrl(mockShortUrl.getShortenedUrl()))
                .thenReturn(mockShortUrl);
        NoPermissionException exception = assertThrows(NoPermissionException.class, () ->
                urlShortenerService.getUrlStats(mockShortUrl.getShortenedUrl(), "bca"));

        assertNotNull(exception);
        assertEquals("You are not allowed to view this URL's stats", exception.getMessage());
    }

    @Test
    void testCreateNewShortUrl(){
        UrlRequest newUrlRequest = new UrlRequest("test.com", LocalDateTime.parse("2025-08-08T12:00:00"));
        String userId = "abc";
        String generatedShortUrl = "abcdef";
        when(shortUrlRepository.findByShortenedUrl(mockShortUrl.getShortenedUrl()))
                .thenReturn(null);
        when(shortUrlRepository.findByOriginalUrl(newUrlRequest.getOriginalUrl()))
                .thenReturn(null);
        when(shortUrlGenerator.getShortUrl())
                .thenReturn(generatedShortUrl);

        ShortUrlEntity createdShortUrl = new ShortUrlEntity();
        createdShortUrl.setOriginalUrl(newUrlRequest.getOriginalUrl());
        createdShortUrl.setExpirationDate(newUrlRequest.getExpiresAt());
        createdShortUrl.setCreatorUserId(userId);

        when(shortUrlRepository.save(any()))
                .thenReturn(createdShortUrl);

        String newShortUrl = urlShortenerService.createShortUrl(newUrlRequest, userId);
        assertEquals(generatedShortUrl, newShortUrl);
    }

}
