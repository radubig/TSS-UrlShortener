package ro.unibuc.hello.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.data.ShortUrlRepository;

import java.security.SecureRandom;
import java.util.Optional;

@Component
public class ShortUrlGenerator {
    @Autowired
    private ShortUrlRepository shortUrlRepository;
    private static final String Base62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int shortUrlLength = 6;
    private static final SecureRandom Random = new SecureRandom();

    public String getShortUrl(){
        StringBuilder shortUrl = new StringBuilder();
        boolean shortUrlReady = false;
        while(!shortUrlReady) {
            for (int urlCharIndex = 0; urlCharIndex < shortUrlLength; urlCharIndex++) {
                shortUrl.append(Base62.charAt(Random.nextInt(62)));
            }
            if(Optional.ofNullable(shortUrlRepository.findByShortenedUrl(shortUrl.toString())).isEmpty())
                shortUrlReady = true;
        }
        return shortUrl.toString();
    }
}
