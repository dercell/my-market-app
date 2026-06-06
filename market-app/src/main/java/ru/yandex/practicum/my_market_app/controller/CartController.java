package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;
import ru.yandex.practicum.my_market_app.service.CartService;

import java.text.MessageFormat;
import java.util.List;


@Controller
@AllArgsConstructor
@RequestMapping
public class CartController {

    private final CartService cartService;


    @GetMapping("/cart/items")
    public Mono<Rendering> getCartItems() {

        return cartService.getCart()
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum())
                        .build()
                );

    }

    @PostMapping("/cart/items")
    public Mono<Rendering> changeItemAmount(@ModelAttribute ItemForm form) {
        if (form.getId() == null || !List.of("MINUS", "PLUS", "DELETE").contains(form.getAction())) {
            return Mono.error(new IllegalArgumentException("Item ID and Action must be specified"));
        }

        return cartService.changeItemAmount(form.getId(), form.getAction())
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
