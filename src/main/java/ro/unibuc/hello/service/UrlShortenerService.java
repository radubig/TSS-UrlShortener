package ro.unibuc.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ro.unibuc.hello.data.ShortUrlEntity;
import ro.unibuc.hello.data.ShortUrlRepository;
import ro.unibuc.hello.dto.UrlRequest;
import ro.unibuc.hello.dto.UrlStats;
import ro.unibuc.hello.exception.NoPermissionException;
import ro.unibuc.hello.exception.ShortUrlNotFoundException;
import ro.unibuc.hello.exception.TooManyEntriesException;
import ro.unibuc.hello.utils.ShortUrlGenerator;
import ro.unibuc.hello.utils.Tracking;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@EnableScheduling
public class UrlShortenerService {
    @Autowired
    private ShortUrlRepository shortUrlRepository;
    @Autowired
    private ShortUrlGenerator shortUrlGenerator;
    @Autowired
    private Tracking tracking;

    public String createShortUrl(UrlRequest urlRequest, String userId){
        String originalUrl = urlRequest.getOriginalUrl();
        LocalDateTime expiresAt = urlRequest.getExpiresAt();


        if(originalUrl == null){
            throw new IllegalArgumentException("Url must not be empty");
        }

        Optional<ShortUrlEntity> existingLink = Optional.ofNullable(shortUrlRepository.findByOriginalUrl(originalUrl));
        if(existingLink.isPresent()){
            return existingLink.get().getShortenedUrl();
        }

        if(shortUrlRepository.countByCreatorUserId(userId) >= 10){
            throw new TooManyEntriesException(userId);
        }

        if(expiresAt != null)
            if(expiresAt.isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Expiration date must be in the future");
        }

        ShortUrlEntity newShortUrl = new ShortUrlEntity();
        newShortUrl.setOriginalUrl(originalUrl);
        newShortUrl.setExpirationDate(Objects.requireNonNullElseGet(expiresAt, () -> LocalDateTime.now().plusMonths(1L)));
        newShortUrl.setCreatorUserId(userId);

        String shortUrl = shortUrlGenerator.getShortUrl();

        newShortUrl.setShortenedUrl(shortUrl);

        shortUrlRepository.save(newShortUrl);
        return shortUrl;
    }

    public String getOriginalUrl(String shortUrl){
        String originalUrl = findShortUrl(shortUrl).getOriginalUrl();
        tracking.incrementVisits(shortUrl);
        return originalUrl;
    }

    public void deleteShortUrl(String shortUrl, String userId){
        ShortUrlEntity shortUrlEntity = findShortUrl(shortUrl);

        if(!shortUrlEntity.getCreatorUserId().equals(userId)){
            throw new NoPermissionException("You are not allowed to delete this URL");
        }
        shortUrlRepository.delete(shortUrlEntity);
    }

    public UrlStats getUrlStats(String shortUrl, String userId){
        ShortUrlEntity shortUrlEntity = findShortUrl(shortUrl);
        if(!shortUrlEntity.getCreatorUserId().equals(userId)){
            throw new NoPermissionException("You are not allowed to view this URL's stats");
        }
        return UrlStats.builder()
                .shortUrl(shortUrl)
                .totalVisits(shortUrlEntity.getTotalVisits())
                .build();
    }

    @Scheduled(fixedRate = 60000)
    public void deleteExpiredUrls(){
        shortUrlRepository.deleteByExpirationDateBefore(LocalDateTime.now());
    }

    private ShortUrlEntity findShortUrl(String shortUrl){
        return Optional.ofNullable(shortUrlRepository.findByShortenedUrl(shortUrl))
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));
    }

}
