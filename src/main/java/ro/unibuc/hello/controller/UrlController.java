package ro.unibuc.hello.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.dto.UrlRequest;
import ro.unibuc.hello.dto.UrlStats;
import ro.unibuc.hello.service.UrlShortenerService;
import ro.unibuc.hello.service.UserService;

import java.time.LocalDateTime;
import java.util.Objects;

@Controller
public class UrlController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

    @PostMapping("/api/urls/createShortUrl")
    @ResponseBody
    public ResponseEntity<String> createShortUrl(@RequestBody UrlRequest urlRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String userId = userService.getUserByUsername(username).getId();

        logger.debug(urlRequest.getOriginalUrl() + Objects.toString(urlRequest.getExpiresAt(), "Default expiration date"));

        return ResponseEntity.ok(urlShortenerService.createShortUrl(urlRequest, userId));
    }

    @GetMapping("/{shortUrl}")
    @ResponseBody
    public ResponseEntity<String> getOriginalUrl(@PathVariable String shortUrl) {
        return ResponseEntity.ok(urlShortenerService.getOriginalUrl(shortUrl));
    }

    @DeleteMapping("/api/urls/delete/{shortUrl}")
    @ResponseBody
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String userId = userService.getUserByUsername(username).getId();

        urlShortenerService.deleteShortUrl(shortUrl, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Url deleted successfully");
    }

    @GetMapping("/api/urls/getStats/{shortUrl}")
    @ResponseBody
    public ResponseEntity<UrlStats> getStats(@PathVariable String shortUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String userId = userService.getUserByUsername(username).getId();

        UrlStats urlStats = urlShortenerService.getUrlStats(shortUrl, userId);
        return ResponseEntity.ok(urlStats);
    }
}
