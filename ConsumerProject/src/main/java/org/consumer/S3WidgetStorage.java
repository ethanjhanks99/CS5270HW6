package org.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.consumer.Interfaces.WidgetStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3WidgetStorage implements WidgetStorage {

    private static final Logger log = LoggerFactory.getLogger(S3WidgetStorage.class);
    private final S3Client s3Client;
    private final String storageBucket;
    private final ObjectMapper objectMapper;

    public S3WidgetStorage(S3Client s3Client, String storageBucket) {
        this.s3Client = s3Client;
        this.storageBucket = storageBucket;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void save(Widget widget) throws Exception {

        String formattedOwner = widget.getOwner().replace(' ', '-').toLowerCase();
        String s3Key = String.format("widgets/%s/%s", formattedOwner, widget.getWidgetId());
        log.info("Preparing to save widget: {}", s3Key);

        try {
            String widgetJson = objectMapper.writeValueAsString(widget);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(storageBucket)
                    .key(s3Key)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromString(widgetJson));
            log.info("Successfully saved widget: {} to S3 bucket {}", s3Key, storageBucket);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize widget: {} to JSON", s3Key, e);
            throw new Exception("Failed to serialize widget: " + widget, e);
        } catch (Exception e) {
            log.error("Failed to save widget: {} to S3 bucket {}", s3Key, storageBucket, e);
        }
    }
}
