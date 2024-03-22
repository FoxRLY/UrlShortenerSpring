package com.example.urlshortner.controllers;

import com.example.urlshortner.dto.UrlRequestBody;
import com.example.urlshortner.services.UrlManagerService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final Bucket bucket;
    private final UrlManagerService urlManager;

    /**
     * Перенаправить на ресурс по идентификатору короткой ссылки
     * @param shortUrl Идентификатор короткой ссылки
     */
    @GetMapping("/go-to/{shortUrl}")
    public ResponseEntity<String> redirect(@PathVariable String shortUrl){
            Optional<String> result = urlManager.retrieveUrl(shortUrl);
            if (result.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL is not registered or expired");
            }
            URI destination;
            try {
                log.info(result.get());
                destination = new URL(result.get()).toURI();
            } catch (URISyntaxException | MalformedURLException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Somehow malformed URL made it to the Database");
            }
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location( destination ).body("");
    }

    /**
     * Создать новый идентификатор короткой ссылки
     * @param fullUrl Полная ссылка
     * @return Идентификатор короткой ссылки
     */
    @Operation(summary = "Создать новый идентификатор короткой ссылки", description = "Создает новый идентификатор короткой ссылки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Запрос создан"),
            @ApiResponse(responseCode = "403", description = "доступ запрещен")
    })
    @PostMapping("/create-url")
    public String createShortUrl(@RequestBody UrlRequestBody fullUrl) {
        if(bucket.tryConsume(1)){
            try {
                return urlManager.createNewUrl(fullUrl.getUrl()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong"));
            } catch (MalformedURLException | URISyntaxException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided string is not a valid URL");
            }
        }
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
    }

    /**
     * Получить полную ссылку по идентификатору короткой ссылки
     * @param shortUrl Идентификатор короткой ссылки
     * @return Полная ссылка
     */
    @GetMapping("/get-url/{shortUrl}")
    public String retrieveFullUrl(@PathVariable String shortUrl){
        return urlManager.retrieveUrl(shortUrl).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL is not registered or expired"));
    }
}
