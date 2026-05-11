package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.service.CartService;

import java.text.MessageFormat;


@Controller
@AllArgsConstructor
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;


    @GetMapping
    public String getCartItems(Model model) {

        CartPageDto cart = cartService.getCart();

        model.addAttribute("items", cart.itemsList());
        model.addAttribute("total", cart.totalSum());

        return "cart";
    }

    @PostMapping
    public String changeItemAmount(@RequestParam("id") Long itemId,
                                   @RequestParam("action") String action, Model model) {

        CartPageDto cart = cartService.changeItemAmount(itemId, action);

        model.addAttribute("items", cart.itemsList());
        model.addAttribute("total", cart.totalSum());

        return "cart";
    }

    @PostMapping("/buy")
    public String buy() {

        String resultRedirect = "redirect:/orders/{0}?newOrder=true";
        Long newOrderId = cartService.buy();
        return MessageFormat.format(resultRedirect, newOrderId);
    }

}
