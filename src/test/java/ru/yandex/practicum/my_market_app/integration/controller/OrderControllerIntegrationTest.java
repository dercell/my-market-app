package ru.yandex.practicum.my_market_app.integration.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;


@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class OrderControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_orders.sql"))).block();
    }

    @AfterEach
    void clearUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/clear_orders.sql"))).block();
    }

    @Test
    void getOrders() {

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Витрина магазина");
                    assert html.contains("Заказ №1") && html.contains("Заказ №2");
                });
    }

    @Test
    void getOrderDetail() {

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/orders/" + 2)
                        .queryParam("newOrder", "false")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h2>Заказ №2</h2>");
                    assert html.contains("<h3>Сумма: 15000 руб.</h3>");
                });
    }

}
