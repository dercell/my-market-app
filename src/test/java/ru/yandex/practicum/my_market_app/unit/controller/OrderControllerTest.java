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
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

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

        Flux<OrderPageDto> orderPageDtoList = Flux.fromIterable(List.of(
                new OrderPageDto(
                        1L,
                        List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                                new ItemDto(2L, "item2", "", "", 3, 1)),
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
                    assert html.contains("Витрина магазина");
                    assert html.contains("<b>Сумма: 53 руб.</b>");
                    assert html.contains("item1") && html.contains("item2");
                });
    }

    @Test
    void getOrderDetail() {
        OrderPageDto orderPageDto = new OrderPageDto(
                1L,
                List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                        new ItemDto(2L, "item2", "", "", 3, 1)),
                53L
        );
        when(orderService.getOrderDetail(1L)).thenReturn(Mono.just(orderPageDto));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/orders/" + 2)
                        .queryParam("newOrder", "false")
                        .build()
                )
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("<h2>Заказ №2</h2>");
                    assert html.contains("<h3>Сумма: 53 руб.</h3>");
                });
    }


}
