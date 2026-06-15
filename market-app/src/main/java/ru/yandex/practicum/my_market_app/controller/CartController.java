package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;
import ru.yandex.practicum.my_market_app.util.validation.itemform.ItemFormValid;

import java.text.MessageFormat;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;


    @GetMapping("/cart/items")
    public Mono<Rendering> getCartItems() {
        return cartService.getCart()
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum())
                        .modelAttribute("canPay", cartPageDto.paymentAvailability().isAvailable())
                        .modelAttribute("paymentUnavailableReason", cartPageDto.paymentAvailability().getMessage())
                        .build()
                );

    }

    @PostMapping("/cart/items")
    public Mono<Rendering> changeItemAmount(@ModelAttribute @ItemFormValid ItemForm form) {


        return cartService.changeItemAmount(form.getId(), form.getAction())
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum())
                        .modelAttribute("canPay", cartPageDto.paymentAvailability().isAvailable())
                        .modelAttribute("paymentUnavailableReason", cartPageDto.paymentAvailability().getMessage())
                        .build());
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return orderService.buy()
                .map(id -> MessageFormat.format("redirect:/orders/{0}?newOrder=true", id));
    }

}
