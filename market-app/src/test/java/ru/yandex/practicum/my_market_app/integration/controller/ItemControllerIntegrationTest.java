package ru.yandex.practicum.my_market_app.integration.controller;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MyTestContainers;
import ru.yandex.practicum.my_market_app.config.TestSecurityIntegrationConfig;
import ru.yandex.practicum.my_market_app.util.WithCustomOidcUser;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("controller")
@Tag("integration")
@Testcontainers
@ImportTestcontainers(MyTestContainers.class)
@Import(TestSecurityIntegrationConfig.class)
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_users.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_cart.sql"))
        ).block();
    }

    @AfterEach
    void clearUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_cart.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_users.sql"))
        ).block();
    }

    @Test
    void getItemPage() {

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">X-Wing</h5>"));
                    assertTrue(html.contains("<h5 class=\"card-title\">Venator</h5>"));
                    assertTrue(html.contains("<option value=\"5\" selected=\"selected\">5</option>"));
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

    @Test
    void ivalidAction() {
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", "1")
                        .queryParam("search", "")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "5")
                        .queryParam("sort", "NO")
                        .queryParam("action", "MULTIPLY").build()
                )
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> assertTrue(html.contains("<span>Действие MULTIPLY не поддерживается</span>")));
    }

}
