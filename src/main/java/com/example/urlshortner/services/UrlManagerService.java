package com.example.urlshortner.services;

import com.example.urlshortner.models.ShortenedUrl;
import com.example.urlshortner.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Менеджер URL
 * Создает новые короткие URL и выдает сохраненные полные URL
 */
@Service
@RequiredArgsConstructor
public class UrlManagerService {
    private final UrlShortenerService urlShortener;
    private final UrlRepository urlRepository;

    public Optional<String> createNewUrl(String full_url) {
        try {
            String short_url = urlShortener.createShortUrl(full_url);
            new URI(full_url);
            ShortenedUrl new_entry = new ShortenedUrl(short_url, full_url);
            urlRepository.save(new_entry);
            return Optional.of(short_url);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    public Optional<String> retrieveUrl(String short_url){
        Optional<ShortenedUrl> res = urlRepository.findById(short_url);
        return res.map(ShortenedUrl::getFull_url).or(Optional::empty);
    }
}
