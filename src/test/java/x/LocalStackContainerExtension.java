package x;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

public class LocalStackContainerExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static final DockerImageName IMAGE_NAME = DockerImageName.parse("localstack/localstack:4.4.0");

    public static final LocalStackContainer CONTAINER;

    static {
        CONTAINER = new LocalStackContainer(IMAGE_NAME).withServices(LocalStackContainer.Service.SQS);
    }

    private static void startContainer() {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
        }
    }

    private static void stopContainer() {
        CONTAINER.stop();
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        startContainer();
    }

    @Override
    public void close() throws Throwable {
        stopContainer();
    }
}
