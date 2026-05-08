package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.my_market_app.model.Order;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    Order getCart(){
        return orderRepository.getOrderByStatusEqualsIgnoreCase("NEW").getFirst();
    }

    @Transactional
    Long buy() {
        Order currentOrder = orderRepository.getOrderByStatusEqualsIgnoreCase("NEW").getFirst();
        orderRepository.closeOrder(currentOrder.getId());
        Order newOrder = new Order();
        newOrder.setStatus("NEW");
        Order savedOrder = orderRepository.save(newOrder);
        return savedOrder.getId();
    }
}
