package com.example.urlshortner.controllers;

import com.example.urlshortner.dto.StringControllerResponse;
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

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final Bucket bucket;
    private final UrlManagerService urlManager;

    /**
     * Перенаправляет на ресурс по идентификатору короткой ссылки
     * @param shortUrl Идентификатор короткой ссылки
     */
    @Operation(summary = "Перенаправить на ресурс по идентификатору короткой ссылки", description = "Перенаправляет на указанный короткой ссылкой ресурс")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Перенаправлен на ресурс по ссылке"),
            @ApiResponse(responseCode = "404", description = "Ссылка не зарегистрирована или просрочена"),
    })
    @GetMapping("/go-to/{shortUrl}")
    public ResponseEntity<String> redirect(@PathVariable String shortUrl) {
        String destination = urlManager.retrieveUrl(shortUrl).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL is not registered or expired"));
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create(destination)).body("");
    }

    /**
     * Создает новый идентификатор короткой ссылки и возвращает его
     * @param fullUrl Полная ссылка
     * @return Идентификатор короткой ссылки
     */
    @Operation(summary = "Создать новый идентификатор короткой ссылки", description = "Создает новый идентификатор короткой ссылки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ссылка создана успешно"),
            @ApiResponse(responseCode = "400", description = "Полученная строка не является валидным URL"),
            @ApiResponse(responseCode = "429", description = "Слишком много запросов")
    })
    @PostMapping("/create-url")
    public StringControllerResponse createShortUrl(@RequestBody UrlRequestBody fullUrl) {
        if(bucket.tryConsume(1)){
            String result =  urlManager.createNewUrl(fullUrl.getUrl()).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided string is not a valid URL"));
            return new StringControllerResponse(result);
        }
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
    }

    /**
     * Возвращает полную ссылку по идентификатору короткой ссылки
     * @param shortUrl Идентификатор короткой ссылки
     * @return Полная ссылка
     */
    @Operation(summary = "Получить полную ссылку по идентификатору короткой ссылки", description = "Выдает сохраненную полную ссылку по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ссылка найдена"),
            @ApiResponse(responseCode = "404", description = "Ссылка не зарегистрирована или просрочена"),
    })
    @GetMapping("/get-url/{shortUrl}")
    public StringControllerResponse retrieveFullUrl(@PathVariable String shortUrl){
        String result = urlManager.retrieveUrl(shortUrl).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL is not registered or expired"));
        return new StringControllerResponse(result);
    }
}
