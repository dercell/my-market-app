package ru.yandex.practicum.my_market_app.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@Testcontainers
public final class MyTestContainers {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test_db")
            .withUsername("junit")
            .withPassword("junit")
            .withReuse(true);

    @Container
    @ServiceConnection
    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:8.2.7-bookworm"))
            .withReuse(true);

    @Container
    static GenericContainer<?> paymentServiceContainer = new GenericContainer<>("payment-service:latest")
            .withExposedPorts(8090)
            .waitingFor(Wait.forHttp("/actuator/health")
                    .forPort(8090)
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(60))
            )
            .withEnv("BALANCE", "100000")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        String url = "http://" + paymentServiceContainer.getHost()
                + ":" + paymentServiceContainer.getMappedPort(8090);

        registry.add("payment-servie.base-url", () -> url);
    }

    static {
        mysql.start();
        redisContainer.start();
        paymentServiceContainer.start();
    }


}
