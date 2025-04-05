package ro.unibuc.hello.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ShortUrlRepository extends MongoRepository<ShortUrlEntity, String> {
    ShortUrlEntity findByOriginalUrl(String originalUrl);

    ShortUrlEntity findByShortenedUrl(String shortenedUrl);

    @Transactional
    void deleteByExpirationDateBefore(LocalDateTime expirationDate);
}
