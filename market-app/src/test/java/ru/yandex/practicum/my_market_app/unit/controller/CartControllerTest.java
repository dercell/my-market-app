package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.config.TestSecurityUnitConfig;
import ru.yandex.practicum.my_market_app.controller.CartController;
import ru.yandex.practicum.my_market_app.model.dto.page.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;
import ru.yandex.practicum.my_market_app.service.UserService;
import ru.yandex.practicum.my_market_app.util.WithCustomOidcUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@Tag("controller")
@Tag("unit")
@WebFluxTest(CartController.class)
@EnableCaching
@Import(TestSecurityUnitConfig.class)
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private UserService userService;

    private CartPageDto cart;

    @BeforeEach
    public void setUp() {
        cart = new CartPageDto(
                List.of(new ItemFullDto(1L, "item1", "", "", 10, 5),
                        new ItemFullDto(2L, "item2", "", "", 3, 1)),
                53L, new PaymentAvailability(true, "")
        );
    }

    @Test
    @WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
    void getCartItems() {

        when(cartService.getCart(1L)).thenReturn(Mono.just(cart));

        webTestClient
                .get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">item1</h5>"));
                    assertTrue(html.contains("<h2>Итого: 53 руб.</h2>"));
                });
    }

    @Test
    void getCartItemsUnauthorized() {
        webTestClient
                .get().uri("/cart/items")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
    void changeItemAmount() {

        when(cartService.changeItemAmount(2L, "PLUS", 1L)).thenReturn(Mono.just(cart));

        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "2")
                        .queryParam("action", "PLUS").build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h5 class=\"card-title\">item1</h5>"));
                    assertTrue(html.contains("<h2>Итого: 53 руб.</h2>"));
                });
    }

    @Test
    void changeItemAmountUnauthorized() {
        webTestClient.post().uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "2")
                        .queryParam("action", "PLUS").build())
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    @WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
    void buy() {
        when(orderService.buy(1L)).thenReturn(Mono.just(3L));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void buyUnauthorized() {

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithCustomOidcUser(username = "luke", userId = 1L, email = "lk@sw.com")
    void buyError() {

        when(orderService.buy(1L)).thenThrow(new RuntimeException("Save error!"));

        webTestClient.post().uri("/buy")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> assertTrue(html.contains("Save error!")));
    }

}
