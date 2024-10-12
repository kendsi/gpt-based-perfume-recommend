package com.acscent.chatdemo2.service;

import java.util.concurrent.CompletableFuture;
import java.util.Map;

public interface ImWebService {
    CompletableFuture<String> getAccessToken();
    CompletableFuture<Map<String, Object>> getOrders(String accessToken);
}
