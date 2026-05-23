package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Mono<OrderPageDto> getOrderDetail(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No order found with id: " + id)))
                .map(OrderService::getOrderInfo);
    }

    public Flux<OrderPageDto> getOrders() {
        return orderRepository.findAll()
                .map(OrderService::getOrderInfo);
    }

    @Transactional
    public Mono<Long> buy(Flux<CartItem> cartItemList) {
        return cartItemList
                .map(cartItem -> OrderItems
                        .builder()
                        .item(cartItem.getItem())
                        .count(cartItem.getCount())
                        .build())
                .collectList()
                .flatMap(
                        orderItemsList -> {
                            Order newOrder = new Order();
                            long totalSum = orderItemsList.stream().mapToLong(
                                    orderItem -> orderItem.getCount() * orderItem.getItem().getPrice()
                            ).sum();
                            newOrder.setOrderItems(orderItemsList);
                            newOrder.setTotalSum(totalSum);
                            return Mono.just(newOrder);
                        })
                .flatMap(orderRepository::save)
                .map(Order::getId);
    }

    private static OrderPageDto getOrderInfo(Order order) {
        List<ItemDto> cartItems = new ArrayList<>();
        for (OrderItems orderItem : order.getOrderItems()) {
            Item cartItem = orderItem.getItem();
            cartItems.add(ItemMapper.toDto(cartItem, orderItem.getCount()));
        }
        return new OrderPageDto(order.getId(), cartItems, order.getTotalSum());
    }
}
