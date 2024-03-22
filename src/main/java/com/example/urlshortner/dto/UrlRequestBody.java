package com.example.urlshortner.dto;

import lombok.Data;

/**
 * Тело запроса с полной ссылкой
 */
@Data
public class UrlRequestBody {
    private String url;
}
