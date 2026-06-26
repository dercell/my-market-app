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
import ru.yandex.practicum.my_market_app.controller.ItemController;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;
import ru.yandex.practicum.my_market_app.util.WithCustomOidcUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Tag("controller")
@Tag("unit")
@WebFluxTest(ItemController.class)
@Import(TestSecurityUnitConfig.class)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    @WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
    void getItemPage() {

        ItemPageDto itemPageDto = new ItemPageDto(
                List.of(List.of(new ItemFullDto(1L, "item1", "", "", 10, 5),
                        new ItemFullDto(2L, "item2", "", "", 3, 1))),
                "",
                "NO",
                new PageDto(0, 5, false, false)
        );

        when(itemService.getItemsPage(1L, "", 0, 5, "NO"))
                .thenReturn(Mono.just(itemPageDto));

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">item1</h5>"));
                    assertTrue(html.contains("<h5 class=\"card-title\">item2</h5>"));
                    assertTrue(html.contains("<option value=\"5\" selected=\"selected\">5</option>"));
                });
    }

    @Test
    void getItemPageAnon() {

        ItemPageDto itemPageDto = new ItemPageDto(
                List.of(List.of(new ItemFullDto(1L, "item1", "", "", 10, 0),
                        new ItemFullDto(2L, "item2", "", "", 3, 0))),
                "",
                "NO",
                new PageDto(0, 5, false, false)
        );

        when(itemService.getItemsPage(-1L, "", 0, 5, "NO"))
                .thenReturn(Mono.just(itemPageDto));

        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">item1</h5>"));
                    assertTrue(html.contains("<h5 class=\"card-title\">item2</h5>"));
                    assertFalse(html.contains("<button type=\"submit\" class=\"btn btn-outline-secondary\" name=\"action\" value=\"MINUS\">-\n" +
                            "                                </button>"));
                });
    }

    @Test
    @WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
    void changeItemAmount() {

        when(itemService.changeItemAmount(anyLong(), anyString(), anyLong())).thenReturn(Mono.empty());

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
    void changeItemAmountAnon() {

        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", "1")
                        .queryParam("search", "")
                        .queryParam("pageNumber", "0")
                        .queryParam("pageSize", "5")
                        .queryParam("sort", "NO")
                        .queryParam("action", "PLUS").build()
                )
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
