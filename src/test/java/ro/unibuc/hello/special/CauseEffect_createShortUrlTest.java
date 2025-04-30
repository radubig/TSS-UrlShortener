package ro.unibuc.hello.special;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ro.unibuc.hello.data.ShortUrlEntity;
import ro.unibuc.hello.data.ShortUrlRepository;
import ro.unibuc.hello.dto.UrlRequest;
import ro.unibuc.hello.exception.TooManyEntriesException;
import ro.unibuc.hello.service.UrlShortenerService;
import ro.unibuc.hello.utils.ShortUrlGenerator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CauseEffect_createShortUrlTest {
    @Mock
    private ShortUrlRepository shortUrlRepository;
    @Mock
    private ShortUrlGenerator shortUrlGenerator;
    @InjectMocks
    private UrlShortenerService urlShortenerService;
    private ShortUrlEntity mockShortUrl = new ShortUrlEntity();
    private final static String testUserId = "testId";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockShortUrl.setId("alreadyExists");
        mockShortUrl.setShortenedUrl("K8sjNf");
        mockShortUrl.setOriginalUrl("www.google.com");
        mockShortUrl.setCreatorUserId(testUserId);
        mockShortUrl.setTotalVisits(0L);
        mockShortUrl.setExpirationDate(LocalDateTime.now().plusMonths(1));

        // Boundary values
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(9);
    }

    // Boundary value for future time
    public static LocalDateTime getFutureTime() {
        return LocalDateTime.now().plusSeconds(1);
    }

    // Boundary value for past time
    public static LocalDateTime getPastTime() {
        return LocalDateTime.now().minusSeconds(1);
    }

    // C1 => Ef1
    @Test
    public void CETest_01()
    {
        // Setup causes
        UrlRequest urlRequest = new UrlRequest(null, getFutureTime());

        // Verify effect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("Url must not be empty", exception.getMessage());
    }

    // C1, C4 => Ef1
    @Test
    public void CETest_02()
    {
        // Setup causes
        UrlRequest urlRequest = new UrlRequest(null, getPastTime());

        // Verify effect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("Url must not be empty", exception.getMessage());
    }

    // C1, C3 => Ef1
    @Test
    public void CETest_03()
    {
        // Setup causes
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(10);
        UrlRequest urlRequest = new UrlRequest(null, getFutureTime());

        // Verify effect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("Url must not be empty", exception.getMessage());
    }

    // C1, C3, C4 => Ef1
    @Test
    public void CETest_04()
    {
        // Setup causes
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(10);
        UrlRequest urlRequest = new UrlRequest(null, getPastTime());

        // Verify effect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("Url must not be empty", exception.getMessage());
    }

    // C2 => Ef2
    @Test
    public void CETest_05()
    {
        // Setup causes
        UrlRequest urlRequest = new UrlRequest("www.google.com", getFutureTime());
        when(shortUrlRepository.findByOriginalUrl("www.google.com")).thenReturn(mockShortUrl);

        // Verify effect
        String shortUrl = urlShortenerService.createShortUrl(urlRequest, testUserId);
        assertEquals(shortUrl, mockShortUrl.getShortenedUrl());
    }

    // C2, C4 => Ef2
    @Test
    public void CETest_06()
    {
        // Setup causes
        UrlRequest urlRequest = new UrlRequest("www.google.com", getPastTime());
        when(shortUrlRepository.findByOriginalUrl("www.google.com")).thenReturn(mockShortUrl);

        // Verify effect
        String shortUrl = urlShortenerService.createShortUrl(urlRequest, testUserId);
        assertEquals(shortUrl, mockShortUrl.getShortenedUrl());
    }

    // C2, C3 => Ef2
    @Test
    public void CETest_07()
    {
        // Setup causes
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(10);
        UrlRequest urlRequest = new UrlRequest("www.google.com", getFutureTime());
        when(shortUrlRepository.findByOriginalUrl("www.google.com")).thenReturn(mockShortUrl);

        // Verify effect
        String shortUrl = urlShortenerService.createShortUrl(urlRequest, testUserId);
        assertEquals(shortUrl, mockShortUrl.getShortenedUrl());
    }

    // C2, C3, C4 => Ef2
    @Test
    public void CETest_08()
    {
        // Setup causes
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(10);
        UrlRequest urlRequest = new UrlRequest("www.google.com", getPastTime());
        when(shortUrlRepository.findByOriginalUrl("www.google.com")).thenReturn(mockShortUrl);

        // Verify effect
        String shortUrl = urlShortenerService.createShortUrl(urlRequest, testUserId);
        assertEquals(shortUrl, mockShortUrl.getShortenedUrl());
    }

    // C3 => Ef3
    @Test
    public void CETest_09()
    {
        // Setup causes
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(10);
        UrlRequest urlRequest = new UrlRequest("www.google.com", getFutureTime());

        // Verify effect
        TooManyEntriesException exception = assertThrows(TooManyEntriesException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("The user " + testUserId + " has reached their short url creation limit", exception.getMessage());
    }

    // C3, C4 => Ef3
    @Test
    public void CETest_10()
    {
        // Setup causes
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(10);
        UrlRequest urlRequest = new UrlRequest("www.google.com", getPastTime());

        // Verify effect
        TooManyEntriesException exception = assertThrows(TooManyEntriesException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("The user " + testUserId + " has reached their short url creation limit", exception.getMessage());
    }

    // C4 => Ef4
    @Test
    public void CETest_11()
    {
        // Setup causes
        UrlRequest urlRequest = new UrlRequest("www.google.com", getPastTime());

        // Verify effect
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                urlShortenerService.createShortUrl(urlRequest, testUserId));
        assertEquals("Expiration date must be in the future", exception.getMessage());
    }

    // No causes => Ef5
    @Test
    public void CETest_12()
    {
        // Setup causes
        UrlRequest urlRequest = new UrlRequest("www.example.com", getFutureTime());
        when(shortUrlRepository.findByOriginalUrl("www.example.com")).thenReturn(null);
        when(shortUrlRepository.countByCreatorUserId(testUserId)).thenReturn(0);
        when(shortUrlGenerator.getShortUrl()).thenReturn("K8sjNf");

        // Verify effect
        String shortUrl = urlShortenerService.createShortUrl(urlRequest, testUserId);
        assertEquals("K8sjNf", shortUrl);
    }
}
