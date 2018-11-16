package com.aurea.client;

import com.aurea.setting.CrossOverSettings;

import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpRestTemplate {
    private static final String X_AUTH_TOKEN = "X-Auth-Token";
    private final transient RestTemplate restTemplate;
    private final transient CrossOverSettings settings;

    HttpRestTemplate(final RestTemplate restTemplate, final CrossOverSettings settings) {
        this.restTemplate = restTemplate;
        this.settings = settings;
    }

    private HttpHeaders createHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.put(X_AUTH_TOKEN, Collections.singletonList(settings.getToken()));
        return headers;
    }

    public <T> ResponseEntity<T> getResponse(final String url, final HttpMethod method, final Object body,
            final Class<T> responseType, final Object... uriVariables) {
        return restTemplate.exchange(url, method, new HttpEntity<>(body, createHeaders()), responseType, uriVariables);
    }

}
