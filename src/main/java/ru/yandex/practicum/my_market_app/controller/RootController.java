package ru.yandex.practicum.my_market_app.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.MessageFormat;

@Controller
@AllArgsConstructor
@RequestMapping
public class RootController {

    @GetMapping
    public String itemPageRedirect(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber
    ) {

        String redirectionString = "redirect:/items?search={0}&sort={1}&pageNumber={2}&pageSize={3}";
        return MessageFormat.format(redirectionString, search, sort, pageNumber, pageSize);
    }

    @PostMapping("/buy")
    public String buyRedirect() {
        return "forward:/cart/items/buy";
    }

}
