package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.my_market_app.model.Order;
import ru.yandex.practicum.my_market_app.repository.OrderItemsRepository;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;

@Service
@AllArgsConstructor
public class OrderItemsService {

    private final OrderItemsRepository orderItemsRepository;
    private final OrderRepository orderRepository;

    void changeItemAmount(Long itemId, String action) {
        Order currentOrder = orderRepository.getOrderByStatusEqualsIgnoreCase("NEW").getFirst();
        int count = "PLUS".equalsIgnoreCase(action) ? 1 : -1;
        orderItemsRepository.changeItemAmount(currentOrder.getId(), itemId, count);
    }


}
