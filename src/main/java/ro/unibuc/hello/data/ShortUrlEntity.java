package ro.unibuc.hello.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlEntity
{
    @Id
    private String id;
    private String originalUrl;
    private String shortenedUrl;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expirationDate;
    private Long totalVisits = 0L;
    private String creatorUserId;
}
