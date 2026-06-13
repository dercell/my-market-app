package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.controller.CartController;
import ru.yandex.practicum.my_market_app.model.dto.page.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.payment.PaymentAvailability;
import ru.yandex.practicum.my_market_app.service.CartService;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Tag("controller")
@Tag("unit")
@WebFluxTest(CartController.class)
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

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
    void getCartItems() {

        when(cartService.getCart()).thenReturn(Mono.just(cart));

        webTestClient.get().uri("/cart/items")
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
    void changeItemAmount() {

        when(cartService.changeItemAmount(2L, "PLUS")).thenReturn(Mono.just(cart));

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

//    @Test
//    void buy() {
//
//        when(cartService.buy()).thenReturn(Mono.just(3L));
//
//        webTestClient.post().uri("/buy")
//                .exchange()
//                .expectStatus().is3xxRedirection();
//    }
//
//    @Test
//    void buyError() {
//
//        when(cartService.buy()).thenThrow(new RuntimeException("Save error!"));
//
//        webTestClient.post().uri("/buy")
//                .exchange()
//                .expectStatus().is5xxServerError()
//                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
//                .expectBody(String.class)
//                .value(html -> {
//                    assertTrue(html.contains("Save error!"));
//                });
//    }

}
