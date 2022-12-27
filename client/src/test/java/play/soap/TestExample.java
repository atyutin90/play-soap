package play.soap;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class TestExample {

    @Test
    public void test() {
        assertTrue(true);
    }

   @Rule
    public GenericContainer underTest = new GenericContainer(
        DockerImageName.parse("play/soap-test-server:0.0.0")
    ).withExposedPorts(8080);

    @Test
    @Order(1)
    public void checkContainer() {
        assertTrue(underTest.isRunning());
    }

    @Test
    @Order(2)
    public void sayHello() {
        assertNotNull(await(withClient(client -> client.sayHello("Alex"), underTest)));
    }

    @Test
    @Order(3)
    public void sayHelloToMany() {
        assertNotNull(await(withClient(client -> client.sayHelloToMany(Arrays.asList("Alex", "John")), underTest)));
    }

    @Test
    @Order(3)
    public void sayHelloToUser() {
        User user = new User();
        user.setName("Alex");
        assertNull(await(withClient(client -> client.sayHelloToUser(user), underTest)));
    }

    private static <T> T withClient(Function<HelloWorld, T> block, GenericContainer underTest) {
        PlayJaxWsProxyFactoryBean factory = new PlayJaxWsProxyFactoryBean();
        factory.setServiceClass(HelloWorld.class);
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setAddress(format("http://%s:%s/helloWorld", underTest.getHost(), underTest.getMappedPort(8080)));
        HelloWorld client = (HelloWorld) factory.create();
        return block.apply(client);
    }

    private static <T> T await(CompletionStage<T> promise) {
        try {
            return promise.toCompletableFuture().get(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
