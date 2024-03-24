package com.example.urlshortner.services;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Укорачиватель URL
 * Создает короткий идентификатор строки по ее наполнению с помощью хэширования
 * Операция идемпотентна
 */
@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    @Value("${short-url.length}")
    private Integer length;

    public String createShortUrl(String full_url){
        String result = BaseEncoding.base64Url().encode(
                Hashing.sha256().hashString(full_url, StandardCharsets.UTF_8).asBytes());
        return result.substring(0, Math.min(result.length(), length));
    }
}
