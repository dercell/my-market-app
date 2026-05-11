package ru.yandex.practicum.my_market_app.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.text.MessageFormat;

@Controller
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public String getItemPage(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            Model model) {

        ItemPageDto itemPageDto = itemService.getItemsPage(search, pageNumber, pageSize, sort);

        model.addAttribute("items", itemPageDto.items());
        model.addAttribute("search", itemPageDto.search());
        model.addAttribute("sort", itemPageDto.sort());
        model.addAttribute("paging", itemPageDto.paging());

        return "items";
    }

    @PostMapping
    public String changeItemAmount(
            @RequestParam("id") Long itemId,
            @RequestParam("search") String search,
            @RequestParam("sort") String sort,
            @RequestParam("pageSize") int pageSize,
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("action") String action
    ) {

        String redirectionString = "redirect:/items?search={0}&sort={1}&pageNumber={2}&pageSize={3}";
        itemService.changeItemAmount(itemId, action);

        return MessageFormat.format(redirectionString, search, sort, pageNumber, pageSize);
    }

}
