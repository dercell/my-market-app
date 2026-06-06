package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.model.entity.Order;
import ru.yandex.practicum.my_market_app.model.entity.OrderItems;
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

    public Mono<OrderPageDto> getOrderDetail(Long id) {
        Mono<Order> orderMono = orderRepository.findById(id);
        Mono<List<ItemDto>> orderItemsFlux = itemDao.getOrderItems(id).collectList();

        return Mono.zip(orderMono, orderItemsFlux).flatMap(tuple2 -> Mono.just(
                new OrderPageDto(tuple2.getT1().getId(), tuple2.getT2(), tuple2.getT1().getTotalSum()))
        );
    }

    public Flux<OrderPageDto> getOrders() {
        return orderRepository.findAll()
                .flatMap(order -> itemDao
                        .getOrderItems(order.getId())
                        .collectList()
                        .map(itemDtoList -> new OrderPageDto(order.getId(), itemDtoList, order.getTotalSum()))
                );
    }

    @Transactional
    public Mono<Long> buy() {
        return itemDao.getItemsInCart()
                .collectList()
                .zipWhen(itemDtoList -> {
                    Order newOrder = new Order();
                    long totalSum = itemDtoList.stream().mapToLong(
                            itemDto -> itemDto.price() * itemDto.count()
                    ).sum();
                    newOrder.setTotalSum(totalSum);
                    return orderRepository.save(newOrder).map(Order::getId);
                })
                .flatMap(tuple2 ->
                        {
                            List<OrderItems> orderItemsList = tuple2.getT1().stream().map(itemDto -> OrderItems
                                    .builder().itemId(itemDto.id()).orderId(tuple2.getT2()).count(itemDto.count()).build()
                            ).toList();
                            return orderItemRepository.saveAll(orderItemsList).then(Mono.just(tuple2.getT2()));
                        }
                );

    }
}
