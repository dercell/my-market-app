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
import ru.yandex.practicum.my_market_app.config.MyTestContainers;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MyTestContainers.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CartControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.sql("delete from cart").fetch().first().block();
        databaseClient.sql("insert into cart(id, item_id, count) values (1,1,1), (2,2,2)").fetch().first().block();
    }

    @AfterEach
    void clearUp() {
        databaseClient.sql("delete from cart").fetch().first().block();
        databaseClient.sql("alter table cart auto_increment = 1").fetch().first().block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/clear_orders.sql"))).block();
    }

    @Test
    void getCartItems() {
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">X-Wing</h5>"));
                    assertTrue(html.contains("<h2>Итого: 27000 руб.</h2>"));
                });

    }

    @Test
    void changeItemAmount() {
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "2")
                        .queryParam("action", "PLUS").build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">X-Wing</h5>"));
                    assertTrue(html.contains("<h2>Итого: 38000 руб.</h2>"));
                });
    }

    @Test
    void buy() {
        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}
