package com.example.urlshortner.services;

import com.example.urlshortner.dto.ShortenedUrl;
import com.example.urlshortner.repository.UrlRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlManagerService {
    private final UrlShortenerService urlShortener;
    private final UrlRepository urlRepository;
    @Transactional
    public Optional<String> createNewUrl(String full_url) throws MalformedURLException, URISyntaxException {
        String short_url = urlShortener.createShortUrl(full_url);
        new URL(full_url).toURI();
        ShortenedUrl new_entry = new ShortenedUrl(short_url, full_url);
        urlRepository.save(new_entry);
        return Optional.of(short_url);
    }

    public Optional<String> retrieveUrl(String short_url){
        Optional<ShortenedUrl> res = urlRepository.findById(short_url);
        return res.map(ShortenedUrl::getFull_url).or(Optional::empty);
    }
}
