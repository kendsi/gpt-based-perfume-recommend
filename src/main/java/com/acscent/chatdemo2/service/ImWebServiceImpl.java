package com.acscent.chatdemo2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ImWebServiceImpl implements ImWebService {

    private final RestTemplate restTemplate;
    private String accessToken; // 인증 토큰 저장
    private long tokenExpiryTime; // 토큰 만료 시간 관리

    @Value("${imweb.api.key}")
    private String imWebApiKey;

    @Value("${imweb.secret.key}")
    private String imWebSecretKey;

    @Override
    public String getProduct() {
        String authUrl = "https://api.imweb.me/v2/auth";
        String productsUrl = "https://api.imweb.me/v2/shop/orders";

        // 토큰이 없거나 만료된 경우 토큰 요청
        if (accessToken == null || System.currentTimeMillis() > tokenExpiryTime) {
            authenticate(authUrl);
        }

        // 이후 토큰을 사용하여 API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access-token", accessToken); // 저장된 토큰 사용

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> productsResponse = restTemplate.exchange(productsUrl, HttpMethod.GET, entity, Map.class);

        if (productsResponse.getStatusCode() == HttpStatus.OK) {
            return productsResponse.getBody().toString();
        } else {
            throw new RuntimeException("Failed to fetch products data: " + productsResponse.getStatusCode());
        }
    }

    // 인증을 통해 토큰을 받아오는 메서드
    private void authenticate(String authUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("key", imWebApiKey);
        authRequest.put("secret", imWebSecretKey);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(authRequest, headers);
        ResponseEntity<Map> authResponse = restTemplate.postForEntity(authUrl, requestEntity, Map.class);

        if (authResponse.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = authResponse.getBody();
            accessToken = (String) body.get("access_token");
            tokenExpiryTime = System.currentTimeMillis() + (Long.parseLong(body.get("expires_in").toString()) * 1000); // 토큰 만료 시간 계산
        } else {
            throw new RuntimeException("Failed to authenticate: " + authResponse.getStatusCode());
        }
    }
}

