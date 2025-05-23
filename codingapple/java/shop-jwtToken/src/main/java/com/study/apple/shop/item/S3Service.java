package com.study.apple.shop.item;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${SPRING_CLOUD_AWS_S3_BUCKET}")
    private String bucket;
    private final S3Presigner s3Presigner;

    String createPresignedUrl(String path) {
        try{
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .build();
            var preSignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(3))
                    .putObjectRequest(putObjectRequest)
                    .build();
            return s3Presigner.presignPutObject(preSignRequest).url().toString();
        }catch (Exception e) {
            throw new RuntimeException("Failed to create presigned URL for path: " + path, e);
        }
    }



}