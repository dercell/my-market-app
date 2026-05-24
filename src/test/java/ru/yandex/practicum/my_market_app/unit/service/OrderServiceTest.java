package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.my_market_app.dao.ItemDao;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.model.entity.Order;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("service")
@Tag("unit")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemDao itemDao;

    @Test
    void getOrderDetail() {
        Order order = Order.builder().id(1L).totalSum(5L).build();
        ItemDto itemDto = new ItemDto(1L, "item1", "", "", 10, 5);

        List<ItemDto> orderItemsList = List.of(itemDto);

        when(orderRepository.findById(1L)).thenReturn(Mono.just(order));
        when(itemDao.getOrderItems(1L)).thenReturn(Flux.fromIterable(orderItemsList));

        OrderPageDto orderPageDto = orderService.getOrderDetail(1L).block();

        assertEquals(order.getId(), orderPageDto.id());
        assertEquals(order.getTotalSum(), orderPageDto.items().size());
        assertEquals(order.getTotalSum(), orderPageDto.totalSum());
    }

    @Test
    void getOrders() {

        List<ItemDto> orderItems1 = List.of(new ItemDto(1L, "item1", "", "", 5L, 1));
        List<ItemDto> orderItems2 = List.of(
                new ItemDto(2L, "item2", "", "", 3L, 1),
                new ItemDto(3L, "item3", "", "", 7L, 1)
        );


        List<Order> orders = List.of(
                Order.builder().id(1L).totalSum(5L).build(),
                Order.builder().id(2L).totalSum(10L).build()
        );

        when(orderRepository.findAll()).thenReturn(Flux.fromIterable(orders));
        when(itemDao.getOrderItems(1L)).thenReturn(Flux.fromIterable(orderItems1));
        when(itemDao.getOrderItems(2L)).thenReturn(Flux.fromIterable(orderItems2));

        List<OrderPageDto> orderPageDtos = orderService.getOrders().collectList().block();

        assertEquals(orders.size(), orderPageDtos.size());
        assertEquals(orders.getLast().getTotalSum(), orderPageDtos.getLast().totalSum());
    }

    @Test
    void buy() {

        Order newOrder = Order.builder().id(1L).totalSum(10L).build();

        List<ItemDto> cartItems = List.of(
                new ItemDto(2L, "item2", "", "", 3L, 1),
                new ItemDto(3L, "item3", "", "", 7L, 1)
        );

        when(itemDao.getItemsInCart()).thenReturn(Flux.fromIterable(cartItems));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(newOrder));

        Long newId = orderService.buy().block();
        verify(orderRepository).save(newOrder);

        assertEquals(newOrder.getId(), newId);
    }

}
