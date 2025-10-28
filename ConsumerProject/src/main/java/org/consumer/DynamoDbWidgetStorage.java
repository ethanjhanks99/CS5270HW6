package org.consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

import org.consumer.Interfaces.WidgetStorage;

public class DynamoDbWidgetStorage implements WidgetStorage {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbWidgetStorage.class);
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbWidgetStorage(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public void save(Widget widget) throws Exception {
        log.info("Preparing to save widget: {} to DynamoDB table: {}", widget.getWidgetId(),  tableName);

        try {
            Map<String, AttributeValue> item = new HashMap<>();

            item.put("widget_id", AttributeValue.builder().s(widget.getWidgetId()).build());
            item.put("owner", AttributeValue.builder().s(widget.getOwner()).build());
            item.put("label", AttributeValue.builder().s(widget.getLabel()).build());
            item.put("description", AttributeValue.builder().s(widget.getDescription()).build());

            if (widget.getOtherAttributes() != null) {
                for (Map.Entry<String, String> entry : widget.getOtherAttributes().entrySet()) {
                    item.put(entry.getKey(), AttributeValue.builder().s(entry.getValue()).build());
                }
            }

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(putItemRequest);
            log.info("Successfully saved widget: {} to DynamoDB table: {}", widget.getWidgetId(), tableName);
        } catch (DynamoDbException e) {
            log.error("Failed to save widget: {} to DynamoDB table: {}", widget.getWidgetId(), tableName, e);
            throw new Exception("Failed to save widget: " + widget.getWidgetId(), e);
        }
    }
}
