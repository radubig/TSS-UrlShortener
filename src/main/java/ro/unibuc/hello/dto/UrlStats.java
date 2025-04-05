package ro.unibuc.hello.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Setter
@Getter
public class UrlStats {
    private String shortUrl;
    private Long totalVisits;
}
