package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.service.ItemService;

@Controller
@AllArgsConstructor
@RequestMapping("/items/{id}")
public class ItemDetailController {

    private final ItemService itemService;

    @GetMapping()
    public Mono<Rendering> getItem(@PathVariable("id") Long itemId, Model model) {
        return Mono.just(Rendering.view("item")
                .modelAttribute("item", itemService.getItem(itemId))
                .build());
    }

    @PostMapping
    public Mono<Rendering> changeItemAmount(
            @PathVariable("id") Long itemId,
            @RequestParam("action") String action
    ) {

        return itemService.changeItemAmount(itemId, action)
                .then(itemService.getItem(itemId))
                .map(itemDto -> Rendering
                        .view("item")
                        .modelAttribute("item", itemDto)
                        .build());
    }

}
