package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;
import ru.yandex.practicum.my_market_app.model.entity.CustomOidcUser;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;
import ru.yandex.practicum.my_market_app.util.security.OidcUserHelper;
import ru.yandex.practicum.my_market_app.util.validation.itemform.ItemFormValid;

import java.security.Principal;import java.text.MessageFormat;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;


    @GetMapping("/cart/items")
    public Mono<Rendering> getCartItems(@AuthenticationPrincipal CustomOidcUser authUser) {
        return cartService.getCart(OidcUserHelper.extractUserIdFromOidcUser(authUser))
                .doOnNext(el -> log.info("USER {}", authUser))
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum())
                        .modelAttribute("canPay", cartPageDto.paymentAvailability().isAvailable())
                        .modelAttribute("paymentUnavailableReason", cartPageDto.paymentAvailability().getMessage())
                        .build()
                );

    }

    @PostMapping("/cart/items")
    public Mono<Rendering> changeItemAmount(
            @AuthenticationPrincipal CustomOidcUser authUser,
            @ModelAttribute @ItemFormValid ItemForm form) {

        return cartService.changeItemAmount(form.getId(), form.getAction(), OidcUserHelper.extractUserIdFromOidcUser(authUser))
                .map(cartPageDto -> Rendering.view("cart")
                        .modelAttribute("items", cartPageDto.itemsList())
                        .modelAttribute("total", cartPageDto.totalSum())
                        .modelAttribute("canPay", cartPageDto.paymentAvailability().isAvailable())
                        .modelAttribute("paymentUnavailableReason", cartPageDto.paymentAvailability().getMessage())
                        .build());
    }

    @PostMapping("/buy")
    public Mono<String> buy(@AuthenticationPrincipal CustomOidcUser authUser) {
        return orderService.buy(OidcUserHelper.extractUserIdFromOidcUser(authUser))
                .map(id -> MessageFormat.format("redirect:/orders/{0}?newOrder=true", id));
    }

}
