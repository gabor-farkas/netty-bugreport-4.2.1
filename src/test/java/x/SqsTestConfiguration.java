package x;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import java.util.ArrayList;
import java.util.List;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;
import static x.LocalStackContainerExtension.CONTAINER;

@TestConfiguration
public class SqsTestConfiguration {

    public static final List<String> queueNames = new ArrayList<>();

    @Bean
    public SqsAsyncClient sqsAsyncClient(final AwsRegionProvider awsRegionProvider) {
        final SqsAsyncClient sqsAsyncClient = SqsAsyncClient.builder()
            .endpointOverride(CONTAINER.getEndpointOverride(SQS))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(CONTAINER.getAccessKey(), CONTAINER.getSecretKey())
                )
            )
            .region(Region.EU_WEST_1)
            .build();

        createSqsQueues(sqsAsyncClient);

        return sqsAsyncClient;
    }

    private void createSqsQueues(final SqsAsyncClient sqsAsyncClient) {
        for (int i = 0; i < 20; i++) {
            final String queueName = "queue-" + i;
            sqsAsyncClient.createQueue(CreateQueueRequest.builder()
                .queueName(queueName)
                .build()).join();
            queueNames.add(queueName);
        }
    }
}

