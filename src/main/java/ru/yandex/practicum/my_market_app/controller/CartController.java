package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.service.CartService;

import java.text.MessageFormat;


@Controller
@AllArgsConstructor
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;


    @GetMapping
    public Mono<Rendering> getCartItems() {

        return cartService.getCart()
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum())
                        .build()
                );

    }

    @PostMapping
    public Mono<Rendering> changeItemAmount(@RequestParam("id") Long itemId,
                                            @RequestParam("action") String action) {

        return cartService.changeItemAmount(itemId, action)
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum()).build());
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return cartService.buy()
                .map(id -> MessageFormat.format("redirect:/orders/{0}?newOrder=true", id));
    }

}
