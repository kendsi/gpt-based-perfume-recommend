package com.acscent.chatdemo2.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.StringTokenizer;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import com.acscent.chatdemo2.dto.UserDTO;
import com.acscent.chatdemo2.service.GoogleService;
import com.acscent.chatdemo2.service.GptService;
import com.acscent.chatdemo2.service.IamWebService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    private IamWebService iamWebService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private GptService gptService;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            System.err.println("File not uploaded");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image not uploaded");
        }

        StringTokenizer st = new StringTokenizer(file.getContentType(),"/");

        if (st.nextToken() != "image") {
            System.err.println("File is not image");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is not image type");
        }

        try {
            googleService.uploadImage(file);
            return ResponseEntity.ok("done");
        } catch (IOException e) {
            System.err.println("IOException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }

    @PostMapping("/products")
    public ResponseEntity<String> getUserAuth(@RequestBody UserDTO userData) {
        // String authString;
        // try{
        //     authString = iamWebService.iamWebAuth();
        // } catch(IOException e) {
        //     System.err.println("IOException");
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No User");
        // }
        // System.out.println(authString);

        // try{
        //     iamWebService.getUser(authString);
        // } catch(IOException e1) {
        //     System.err.println("IOException");
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No Response");
        // } catch(ParseException e2) {
        //     System.err.println("ParseException");
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Couldn't Parse to Json");
        // }

        try{
            googleService.findByCode(userData.getCode());
            return ResponseEntity.ok("Done");
        } catch (IOException e) {
            System.err.println("IOException");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot Access to Google Spread Sheets");
        }
    }
}
