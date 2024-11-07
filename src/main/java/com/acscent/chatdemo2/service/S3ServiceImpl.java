package com.acscent.chatdemo2.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acscent.chatdemo2.exceptions.ImageUploadException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public String uploadImage(MultipartFile image, String userName) {
        try {
            String fileExtension = image.getOriginalFilename();
            if (fileExtension != null && fileExtension.contains(".")) {
                // 마지막 점(.) 이후의 문자열을 추출하여 확장자로 사용
                fileExtension = fileExtension.substring(fileExtension.lastIndexOf("."));
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            String formattedDateTime = LocalDateTime.now().format(formatter);
            String imagePath = "images/" + formattedDateTime + "-" + userName + fileExtension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(image.getContentType());
            metadata.setContentLength(image.getSize());

            amazonS3.putObject(new PutObjectRequest(bucketName, imagePath, image.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

            return amazonS3.getUrl(bucketName, imagePath).toString();
        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image: error reading the file input stream.");
        } catch (AmazonServiceException e) {
            throw new ImageUploadException("Failed to upload image to AWS S3: " + e.getMessage());
        } catch (AmazonClientException e) {
            throw new ImageUploadException("AWS S3 client error: " + e.getMessage());
        }
    }
}
