package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.model.entity.Order;
import ru.yandex.practicum.my_market_app.model.entity.OrderItems;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;
import ru.yandex.practicum.my_market_app.util.mappers.ItemMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderPageDto getOrderDetail(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        return getOrderInfo(order);
    }

    public List<OrderPageDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::getOrderInfo).toList();
    }

    @Transactional
    public Long buy(List<CartItem> cartItemList) {
        Order newOrder = new Order();
        AtomicLong totalSum = new AtomicLong(0L);

        List<OrderItems> orderItems = cartItemList.stream()
                .map(cartItem -> {
                    totalSum.addAndGet(cartItem.getItem().getPrice() * cartItem.getCount());
                    return OrderItems
                            .builder()
                            .order(newOrder)
                            .item(cartItem.getItem())
                            .count(cartItem.getCount())
                            .build();
                })
                .toList();
        newOrder.setOrderItems(orderItems);
        newOrder.setTotalSum(totalSum.get());

        Order savedOrder = orderRepository.save(newOrder);
        return savedOrder.getId();
    }

    public OrderPageDto getOrderInfo(Order order) {
        List<ItemDto> cartItems = new ArrayList<>();
        long totalSum = 0L;
        for (OrderItems orderItem : order.getOrderItems()) {
            Item cartItem = orderItem.getItem();
            cartItems.add(ItemMapper.toDto(cartItem, orderItem.getCount()));
            totalSum += cartItem.getPrice() * orderItem.getCount();
        }
        return new OrderPageDto(order.getId(), cartItems, totalSum);
    }
}
