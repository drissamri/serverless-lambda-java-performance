package be.drissamri.performance.services;


import be.drissamri.performance.model.Product;
import be.drissamri.performance.model.ProductRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductServiceImpl implements ProductService {
    private final DynamoDbClient dynamoDbClient;

    @Inject
    public ProductServiceImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public Product addProduct(final ProductRequest productRequest) {
        final String productId = UUID.randomUUID().toString();

        final Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("id", AttributeValue.builder().s(productId).build());
        itemValues.put("name", AttributeValue.builder().s(productRequest.getName()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("products")
                .item(itemValues)
                .build();

        try {
            this.dynamoDbClient.putItem(request);

            return new Product(productId, productRequest.getName());
        } catch (Exception e) {
            throw new RuntimeException("Database update failed!");
        }
    }
}
