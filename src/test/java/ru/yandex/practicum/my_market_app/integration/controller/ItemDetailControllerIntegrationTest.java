package ru.yandex.practicum.my_market_app.integration.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;


@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@Transactional
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ItemDetailControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getItem() {

        webTestClient.get().uri("/items/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h5 class=\"card-title\">X-Wing</h5>");
                    assert html.contains("Лего набор \"X-Wing\"");
                });

    }

    @Test
    void changeItemAmount() {

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items/" + 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h5 class=\"card-title\">X-Wing</h5>");
                    assert html.contains("<span>1</span>");
                });

    }

}
