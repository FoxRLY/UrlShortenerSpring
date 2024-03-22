package com.example.urlshortner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * Запись в редисе в виде короткая_ссылка: полная_ссылка
 */
@AllArgsConstructor
@Data
@RedisHash(timeToLive = 600L)
public class ShortenedUrl {
    @Id
    private String short_url;
    private String full_url;
}