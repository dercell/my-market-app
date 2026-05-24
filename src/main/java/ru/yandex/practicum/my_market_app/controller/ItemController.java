package ru.yandex.practicum.my_market_app.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemForm;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
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
            @ModelAttribute ItemForm form
    ) {
        if (form.getId() == null || !List.of("MINUS", "PLUS").contains(form.getAction())) {
            Mono.error(new IllegalArgumentException("Item ID and Action must be specified"));
        }

        return itemService.changeItemAmount(form.getId(), form.getAction())
                .thenReturn(MessageFormat.format("redirect:/items?search={0}&sort={1}&pageNumber={2}&pageSize={3}",
                        form.getSearch(), form.getSort(), form.getPageNumber(), form.getPageSize()));
    }

}
