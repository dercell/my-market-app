package ru.yandex.practicum.my_market_app.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String getOrders(Model model) {

        List<OrderPageDto> orders = orderService.getOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrderDetail(
            @PathVariable("id") Long orderId,
            @RequestParam(name = "newOrder", required = false, defaultValue = "false") boolean isNewOrder,
            Model model
    ) {

        OrderPageDto pageDto = orderService.getOrderDetail(orderId);

        model.addAttribute("order", pageDto);
        model.addAttribute("newOrder", isNewOrder);
        return "order";
    }

}
