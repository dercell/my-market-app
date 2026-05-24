package ru.yandex.practicum.my_market_app.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.text.MessageFormat;

@Controller
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Mono<Rendering> getItemPage(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber
    ) {

        return itemService.getItemsPage(search, pageNumber, pageSize, sort)
                .map(itemPageDto -> Rendering.view("items")
                        .modelAttribute("items", itemPageDto.items())
                        .modelAttribute("search", itemPageDto.search())
                        .modelAttribute("sort", itemPageDto.sort())
                        .modelAttribute("paging", itemPageDto.paging())
                        .build());
    }

    @PostMapping
    public Mono<String> changeItemAmount(
            @RequestParam("id") Long itemId,
            @RequestParam("search") String search,
            @RequestParam("sort") String sort,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("action") String action
    ) {

        return itemService.changeItemAmount(itemId, action)
                .thenReturn(MessageFormat.format("redirect:/items?search={0}&sort={1}&pageNumber={2}&pageSize={3}",
                        search, sort, pageNumber, pageSize));
    }

}
