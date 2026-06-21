package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.entity.CustomOidcUser;
import ru.yandex.practicum.my_market_app.model.entity.User;
import ru.yandex.practicum.my_market_app.service.OrderService;
import ru.yandex.practicum.my_market_app.util.security.OidcUserHelper;


@Controller
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Mono<Rendering> getOrders(
            @AuthenticationPrincipal CustomOidcUser authUser
    ) {
        return orderService.getOrders(OidcUserHelper.extractUserIdFromOidcUser(authUser))
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
