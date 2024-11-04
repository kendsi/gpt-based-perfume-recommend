package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ImWebServiceImpl implements ImWebService {

    private final RestTemplate restTemplate;

    @Value("${imweb.api.key}")
    private String imWebApiKey;

    @Value("${imweb.secret.key}")
    private String imWebSecretKey;

    @Async
    public CompletableFuture<String> getAccessToken() {
        String authUrl = "https://api.imweb.me/v2/auth";
        Map<String, String> authReqBody = new HashMap<>();
        authReqBody.put("key", imWebApiKey);
        authReqBody.put("secret", imWebSecretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(authReqBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(authUrl, request, Map.class);
            Map<String, String> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return CompletableFuture.completedFuture(responseBody.get("access_token"));
            } else {
                throw new RuntimeException("Failed to retrieve access token");
            }
        } catch (RestClientException e) {
            throw new RuntimeException("IMWEB API Authentication failed", e);
        }
    }

    @Async
    public CompletableFuture<Map<String, Object>> getOrders(String accessToken) {
        String ordersUrl = "https://api.imweb.me/v2/shop/orders";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(ordersUrl, HttpMethod.GET, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                return CompletableFuture.completedFuture(responseBody);
            } else {
                throw new RuntimeException("Failed to retrieve orders");
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch orders from IMWEB API", e);
        }
    }
}

