package org.consumer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "consumer", mixinStandardHelpOptions = true,
        version = "Consumer 1.0",
        description = "Processes widget requests from an S3 bucket.")
public class Main implements Callable<Integer> {


    @Option(names = {"-wb", "--widget-bucket"}, required = true, description = "Name of the S3 bucket that will contain requests (default=null)")
    private String widgetBucket;

    @Option(names = {"-dwt", "--dynamodb-widget-table"}, description = "Name of the DynamoDB table that holds widgets (default=null)")
    private String dynamodbWidgetTable;



    @Override
    public Integer call() throws Exception {
        return 0;
    }

    public static void main(String[] args) {
    int exitCode = new CommandLine(new Main()).execute(args);
    System.exit(exitCode);
    }
}