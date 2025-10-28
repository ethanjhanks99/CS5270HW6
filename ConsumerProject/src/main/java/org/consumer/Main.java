package org.consumer;

import org.consumer.Interfaces.IncomingRequest;
import org.consumer.Interfaces.WidgetStorage;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.concurrent.Callable;

@Command(name = "consumer", mixinStandardHelpOptions = true,
        version = "Consumer 1.0",
        description = "Processes widget requests from an S3 bucket.")
public class Main implements Callable<Integer> {

    enum StorageType {s3, dynamodb}

    @Option(names = {"-s", "--storage-type"}, required = true, description = "Storage strategy. Valid values: ${COMPLETION-CANDIDATES}")
    private StorageType storageType;

    @Option(names = {"-rb", "--request-bucket"}, required = true, description = "Name of bucket that will contain requests (default=null)")
    private String requestBucket = null;

    @Option(names = {"-wb", "--widget-bucket"}, description = "Name of the S3 bucket that holds the widgets (default=null)")
    private String widgetBucket = null;

    @Option(names = {"-dwt", "--dynamodb-widget-table"}, description = "Name of the DynamoDB table that holds widgets (default=null)")
    private String dynamodbTable = null;

    @Option(names = {"-r", "--region"}, description = "Name of AWS region to use (default=us-east-1")
    private String awsRegion = "us-east-1";

    @Option(names = {"-p", "--polling-time"}, description = "Amount of time (in milliseconds) between polling (default=100)")
    private int pollingTime = 100;


    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (storageType == StorageType.s3 && (requestBucket == null || requestBucket.isEmpty())) {
            System.err.println("Error: --widget-bucket is required when --storage-type=s3");
            System.exit(1);
        }
        if (storageType == StorageType.dynamodb && (dynamodbTable == null || widgetBucket.isBlank())) {
            System.err.println("Error: --dynamodb-widget-table is required when --storage-type=dynamodb");
            return 1;
        }

        Region region = Region.of(awsRegion);
        S3Client s3Client = S3Client.builder().region(region).build();
        DynamoDbClient dynamodbClient = DynamoDbClient.builder().region(region).build();

        IncomingRequest request = new S3RequestRetriever(s3Client, requestBucket);

        WidgetStorage storage;
        if (storageType == StorageType.s3) {
            storage = new S3WidgetStorage(s3Client, widgetBucket);
        } else {
            storage = new DynamoDbWidgetStorage(dynamodbClient, dynamodbTable);
        }

        Consumer consumer = new Consumer(request, storage, pollingTime);

        consumer.start();

        s3Client.close();
        dynamodbClient.close();

        return 0;
    }


}