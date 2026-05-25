package ru.yandex.practicum.my_market_app.integration.controller;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;

@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ItemControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getItemPage() {

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h5 class=\"card-title\">X-Wing</h5>");
                    assert html.contains("<h5 class=\"card-title\">Venator</h5>");
                    assert html.contains("<option value=\"5\" selected=\"selected\">5</option>");
                });

    }


    @Test
    void changeItemAmount() {
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", "1")
                        .queryParam("search", "")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "5")
                        .queryParam("sort", "NO")
                        .queryParam("action", "PLUS").build()
                )
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}
