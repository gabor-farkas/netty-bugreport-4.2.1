package x;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;

import java.util.concurrent.CompletableFuture;

@SpringBootTest(properties = {
    "spring.cloud.aws.sqs.enabled=true",
})
@ExtendWith({
    LocalStackContainerExtension.class,
})
@ContextConfiguration(classes = SqsTestConfiguration.class)
public class XTest {

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Test
    public void test() {
        int cnt = 0;
        while (true) {
            final var requests = SqsTestConfiguration.queueNames
                .stream()
                .map(queue -> sqsAsyncClient.purgeQueue(PurgeQueueRequest.builder()
                    .queueUrl(queue)
                    .build()))
                .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(requests).join();
            System.out.println("Purge finished " + (cnt ++));
        }
    }
}
