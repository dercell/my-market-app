package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.config.TestSecurityUnitConfig;
import ru.yandex.practicum.my_market_app.controller.ItemDetailController;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.service.ItemService;
import ru.yandex.practicum.my_market_app.util.WithCustomOidcUser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Tag("controller")
@Tag("unit")
@WebFluxTest(ItemDetailController.class)
@Import(TestSecurityUnitConfig.class)
@WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
class ItemDetailControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void getItem() {

        ItemFullDto itemFullDto = new ItemFullDto(1L, "item1", "", "", 5L, 1);

        when(itemService.getItem(1L, 1L)).thenReturn(Mono.just(itemFullDto));

        webTestClient.get().uri("/items/{id}", 1L)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">item1</h5>"));
                    assertTrue(html.contains("5 руб."));
                });

    }

    @Test
    void changeItemAmount() {

        ItemFullDto itemFullDto = new ItemFullDto(1L, "item1", "", "", 5L, 1);

        when(itemService.getItem(1L, 1L)).thenReturn(Mono.just(itemFullDto));
        when(itemService.changeItemAmount(1L, "PLUS", 1L)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items/" + 1)
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">item1</h5>"));
                    assertTrue(html.contains("<span>1</span>"));
                });
    }

}
