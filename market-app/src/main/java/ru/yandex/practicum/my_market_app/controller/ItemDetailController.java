package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;
import ru.yandex.practicum.my_market_app.model.entity.CustomOidcUser;
import ru.yandex.practicum.my_market_app.model.entity.User;
import ru.yandex.practicum.my_market_app.service.ItemService;
import ru.yandex.practicum.my_market_app.util.security.OidcUserHelper;
import ru.yandex.practicum.my_market_app.util.validation.itemform.ItemFormValid;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/items/{id}")
public class ItemDetailController {

    private final ItemService itemService;

    @GetMapping()
    public Mono<Rendering> getItem(
            @AuthenticationPrincipal CustomOidcUser authUser,
            @PathVariable("id") Long itemId
    ) {
        return Mono.just(Rendering.view("item")
                .modelAttribute("item",
                        Mono.just(OidcUserHelper.extractUserIdFromOidcUser(authUser))
                                .flatMap(userId -> itemService.getItem(itemId, userId)))
                .build());
    }

    @PostMapping
    public Mono<Rendering> changeItemAmount(
            @AuthenticationPrincipal CustomOidcUser authUser,
            @ModelAttribute @ItemFormValid ItemForm form
    ) {
        log.info("Current user {}", authUser.getDbUser());
        return Mono.just(OidcUserHelper.extractUserIdFromOidcUser(authUser))
                .map(userId -> itemService.changeItemAmount(form.getId(), form.getAction(), userId)
                        .then(itemService.getItem(form.getId(), userId)))
                .map(itemDto -> Rendering
                        .view("item")
                        .modelAttribute("item", itemDto)
                        .build());
    }

}
