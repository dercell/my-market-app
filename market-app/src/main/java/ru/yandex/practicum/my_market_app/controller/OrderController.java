package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.service.OrderService;


@Controller
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Mono<Rendering> getOrders() {
        return orderService.getOrders()
                .collectList()
                .map(orderList ->
                        Rendering.view("orders")
                                .modelAttribute("orders", orderList)
                                .build());
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrderDetail(
            @PathVariable("id") Long orderId,
            @RequestParam(name = "newOrder", required = false, defaultValue = "false") boolean isNewOrder
    ) {

        return Mono.just(Rendering
                .view("order")
                .modelAttribute("order", orderService.getOrderDetail(orderId))
                .modelAttribute("newOrder", isNewOrder)
                .build()
        );
    }

}
