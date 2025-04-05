package ro.unibuc.hello.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequest {
    private String originalUrl;
    private LocalDateTime expiresAt;
}
