package com.example.urlshortner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestUrlShortnerApplication {

    public static void main(String[] args) {
        SpringApplication.from(UrlShortnerApplication::main).with(TestUrlShortnerApplication.class).run(args);
    }

}
