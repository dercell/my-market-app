package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/items/{id}")
public class ItemDetailController {

    private final ItemService itemService;

    @GetMapping()
    public Mono<Rendering> getItem(@PathVariable("id") Long itemId) {
        return Mono.just(Rendering.view("item")
                .modelAttribute("item", itemService.getItem(itemId))
                .build());
    }

    @PostMapping
    public Mono<Rendering> changeItemAmount(
            @ModelAttribute ItemForm form
    ) {
        if (form.getId() == null || !List.of("MINUS", "PLUS", "DELETE").contains(form.getAction())) {
            return Mono.error(new IllegalArgumentException("Item ID and Action must be specified"));
        }

        return itemService.changeItemAmount(form.getId(), form.getAction())
                .then(itemService.getItem(form.getId()))
                .map(itemDto -> Rendering
                        .view("item")
                        .modelAttribute("item", itemDto)
                        .build());
    }

}
