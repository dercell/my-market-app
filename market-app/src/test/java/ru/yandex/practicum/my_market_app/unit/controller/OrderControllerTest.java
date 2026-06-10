package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.controller.OrderController;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderDetailDto;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Tag("controller")
@Tag("unit")
@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getOrders() {

        Flux<OrderDetailDto> orderPageDtoList = Flux.fromIterable(List.of(
                new OrderDetailDto(
                        1L,
                        List.of(new ItemFullDto(1L, "item1", "", "", 10, 5),
                                new ItemFullDto(2L, "item2", "", "", 3, 1)),
                        53L
                )
        ));

        when(orderService.getOrders()).thenReturn(orderPageDtoList);

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("Витрина магазина"));
                    assertTrue(html.contains("<b>Сумма: 53 руб.</b>"));
                    assertTrue(html.contains("item1") && html.contains("item2"));
                });
    }

    @Test
    void getOrderDetail() {
        OrderDetailDto orderDetailDto = new OrderDetailDto(
                1L,
                List.of(new ItemFullDto(1L, "item1", "", "", 10, 5),
                        new ItemFullDto(2L, "item2", "", "", 3, 1)),
                53L
        );
        when(orderService.getOrderDetail(1L)).thenReturn(Mono.just(orderDetailDto));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/orders/" + 1)
                        .queryParam("newOrder", "false")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("<h2>Заказ №1</h2>"));
                    assertTrue(html.contains("<h3>Сумма: 53 руб.</h3>"));
                });
    }


}
