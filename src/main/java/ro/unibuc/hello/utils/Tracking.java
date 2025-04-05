package ro.unibuc.hello.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.data.ShortUrlEntity;
import ro.unibuc.hello.data.ShortUrlRepository;

@Component
public class Tracking {
    @Autowired
    private ShortUrlRepository shortUrlRepository;

    public void incrementVisits(String shortUrl){
        ShortUrlEntity shortUrlEntity = shortUrlRepository.findByShortenedUrl(shortUrl);
        shortUrlEntity.setTotalVisits(shortUrlEntity.getTotalVisits() + 1L);
        shortUrlRepository.save(shortUrlEntity);
    }

}
