package org.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import org.consumer.Interfaces.IncomingRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class S3RequestRetriever implements IncomingRequest {

    private static final Logger log = LoggerFactory.getLogger(S3RequestRetriever.class);
    private final S3Client s3Client;
    private final String requestBucket;
    private final ObjectMapper objectMapper;

    public S3RequestRetriever(S3Client s3Client, String requestBucket) {
        this.s3Client = s3Client;
        this.requestBucket = requestBucket;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<WidgetRequest> getNextRequest() {

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(requestBucket)
                .maxKeys(1)
                .build();

        List<S3Object> objects = s3Client.listObjectsV2(listObjectsV2Request).contents();

        if (objects.isEmpty()) {
            return Optional.empty();
        }

        String objectKey = objects.getFirst().key();
        log.info("Retrieving object key: {}", objectKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(requestBucket)
                .key(objectKey)
                .build();

        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
            String responseBody = responseBytes.asUtf8String();

            WidgetRequest widgetRequest = objectMapper.readValue(responseBody, WidgetRequest.class);
            log.info("Retrieved object key: {}", objectKey);

            deleteRequestObject(objectKey);

            return Optional.of(widgetRequest);

        } catch (S3Exception | IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private void deleteRequestObject(String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(requestBucket)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Deleted object key: {}", objectKey);
        } catch (S3Exception e) {
            log.error(e.getMessage());
        }
    }
}
