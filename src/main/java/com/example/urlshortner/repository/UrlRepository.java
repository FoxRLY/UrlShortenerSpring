package com.example.urlshortner.repository;

import com.example.urlshortner.models.ShortenedUrl;
import org.springframework.data.repository.CrudRepository;

public interface UrlRepository extends CrudRepository<ShortenedUrl, String> { }
