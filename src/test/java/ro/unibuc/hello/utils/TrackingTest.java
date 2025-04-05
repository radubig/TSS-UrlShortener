package ro.unibuc.hello.utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ro.unibuc.hello.data.ShortUrlEntity;
import ro.unibuc.hello.data.ShortUrlRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class TrackingTest {
    @InjectMocks
    private Tracking tracking;
    @Mock
    ShortUrlRepository shortUrlRepository;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVisitIncrement(){
        String shortUrl = "abcJ92";
        ShortUrlEntity shortUrlEntity = new ShortUrlEntity();
        shortUrlEntity.setTotalVisits(6L);
        shortUrlEntity.setId("abc");

        when(shortUrlRepository.findByShortenedUrl(shortUrl))
                .thenReturn(shortUrlEntity);

        tracking.incrementVisits(shortUrl);

        ArgumentCaptor<ShortUrlEntity> captor = ArgumentCaptor.forClass(ShortUrlEntity.class);
        verify(shortUrlRepository).save(captor.capture());

        assertEquals(7L, captor.getValue().getTotalVisits());

    }
}
