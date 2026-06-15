package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderItemDto;

import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderDetailDto;
import ru.yandex.practicum.my_market_app.model.entity.Order;
import ru.yandex.practicum.my_market_app.model.entity.OrderItem;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.repository.OrderItemRepository;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemDao itemDao;
    private final PaymentService paymentService;
    private final CartService cartService;

    public Mono<OrderDetailDto> getOrderDetail(Long id) {
        Mono<Order> orderMono = orderRepository.findById(id);
        Mono<List<OrderItemDto>> orderItemsFlux = itemDao.getOrderItems(id).collectList();

        return Mono.zip(orderMono, orderItemsFlux).flatMap(tuple2 -> Mono.just(
                new OrderDetailDto(tuple2.getT1().getId(), tuple2.getT2(), tuple2.getT1().getTotalSum()))
        );
    }

    public Flux<OrderDetailDto> getOrders() {
        return orderRepository.findAll()
                .flatMap(order -> itemDao
                        .getOrderItems(order.getId())
                        .collectList()
                        .map(itemDtoList -> new OrderDetailDto(order.getId(), itemDtoList, order.getTotalSum()))
                );
    }

    @Transactional
    public Mono<Long> buy() {
        return cartService.getCartItems()
                .flatMap(items -> {
                    if (items.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Корзина пуста"));
                    }
                    return saveOrder(items)
                            .flatMap(order -> saveOrderItems(items, order))
                            .flatMap(this::chargeBalance)
                            .flatMap(newOrderId -> cartService.clearCart()
                                    .thenReturn(newOrderId));
                });
    }

    private Mono<Long> chargeBalance(Order order) {
        return paymentService.chargeOrderBalance(order.getId(), order.getTotalSum());
    }

    private Mono<Order> saveOrderItems(List<ItemFullDto> items, Order order) {
        List<OrderItem> orderItemList = items.stream().map(itemDto -> OrderItem
                .builder().itemId(itemDto.getId()).orderId(order.getId()).count(itemDto.getCount()).build()
        ).toList();
        return orderItemRepository.saveAll(orderItemList).then(Mono.just(order));
    }

    private Mono<Order> saveOrder(List<ItemFullDto> itemFullDtoList) {
        Order newOrder = new Order();
        long totalSum = itemFullDtoList.stream().mapToLong(
                itemDto -> itemDto.getPrice() * itemDto.getCount()
        ).sum();
        newOrder.setTotalSum(totalSum);

        return orderRepository.save(newOrder);
    }
}
