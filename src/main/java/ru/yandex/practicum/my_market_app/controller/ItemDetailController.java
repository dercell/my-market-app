package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

@Controller
@AllArgsConstructor
@RequestMapping("/items/{id}")
public class ItemDetailController {

    private final ItemService itemService;

    @GetMapping()
    public String getItem(@PathVariable("id") Long itemId, Model model) {
        ItemDto item = itemService.getItem(itemId).orElseThrow();
        model.addAttribute("item", item);

        return "item";
    }

    @PostMapping
    public String changeItemAmount(
            @PathVariable("id") Long itemId,
            @RequestParam("action") String action,
            Model model
    ) {

        itemService.changeItemAmount(itemId, action);
        ItemDto item = itemService.getItem(itemId).orElseThrow();
        model.addAttribute("item", item);

        return "item";
    }

}
