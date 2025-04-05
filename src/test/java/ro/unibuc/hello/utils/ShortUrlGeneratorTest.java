package ro.unibuc.hello.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.data.ShortUrlRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class ShortUrlGeneratorTest {
    @InjectMocks
    private ShortUrlGenerator shortUrlGenerator;
    @Mock
    private ShortUrlRepository shortUrlRepository;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }
    private static final String BASE62_REGEX = "^[0-9A-Za-z]+$";

    @Test
    void testGenerateShortUrl(){
        when(shortUrlRepository.findByShortenedUrl(anyString())).thenReturn(null);
        String shortUrl = shortUrlGenerator.getShortUrl();
        assertEquals(6, shortUrl.length());
        assertThat(shortUrl).matches(BASE62_REGEX);
    }
}
