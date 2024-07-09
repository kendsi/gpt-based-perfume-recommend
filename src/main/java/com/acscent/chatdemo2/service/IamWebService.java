package com.acscent.chatdemo2.service;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.acscent.chatdemo2.dto.UserDTO;
import com.acscent.chatdemo2.model.User;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class IamWebService {

    @Autowired
    private GoogleService googleService;

    @Value("${iamweb.api.key}")
    private static String API_KEY;

    @Value("${imweb.secret.key}")
    private static String SECRET_KEY;

    public String iamWebAuth() throws IOException {

        OkHttpClient client = new OkHttpClient();

        Map<String, String> req = new HashMap<>();
        req.put("key", API_KEY);
        req.put("secret", SECRET_KEY);

        JSONObject json = new JSONObject(req);

        String url = "https://api.imweb.me/v2/auth";
        RequestBody reqBody = RequestBody.create(json.toJSONString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .post(reqBody)
            .build();

        Response response = client.newCall(request).execute();
        return response.toString().replace("Response", "")
                                .replace("=", "\":\"")
                                .replace(", ", "\", \"")
                                .replace("{", "{\"")
                                .replace("}", "\"}");
    }

    public User getUser(String authResponse) throws ParseException, IOException {

        JSONParser parser = new JSONParser();
        JSONObject authJson = new JSONObject();
        authJson = (JSONObject) parser.parse(authResponse);
        
        String accessToken;
        try {
            accessToken = (String) authJson.get("access_token");
            System.out.println(accessToken);
        } catch (Exception e) {
            throw new IOException();
        }

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.imweb.me/v2/member/members";

        Request request = new Request.Builder()
            .url(url)
            .header("Content-Type", "application/json")
            .header("access-token", accessToken)
            .build();
        
        Response response = client.newCall(request).execute();
        String userString = response.toString();

        JSONObject userJson = new JSONObject();
        userJson = (JSONObject) parser.parse(userString);

        String email;
        String memberCode;
        try {
            JSONObject data = (JSONObject) userJson.get("data");
            email = (String) data.get("email");
            memberCode = (String) data.get("member_code");
        } catch (Exception e) {
            throw new IOException();
        }

        return new User(email, memberCode);
    }
}
