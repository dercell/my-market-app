package ru.yandex.practicum.my_market_app.integration.controller;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;


@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@Transactional
@Sql(statements = "insert into cart(id, item_id, count) values (1,1,1), (2,2,2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(statements = "truncate table cart", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(value = "classpath:db/scripts/clear_orders.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CartControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getCartItems() {
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h5 class=\"card-title\">X-Wing</h5>");
                    assert html.contains("<h2>Итого: 27000 руб.</h2>");
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
                    assert html.contains("<h5 class=\"card-title\">X-Wing</h5>");
                    assert html.contains("<h2>Итого: 38000 руб.</h2>");
                });
    }

    @Test
    void buy() {
        webTestClient.post().uri("/cart/items/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}
