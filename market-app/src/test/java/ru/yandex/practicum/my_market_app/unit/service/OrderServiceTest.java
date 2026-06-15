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
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderDetailDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderItemDto;
import ru.yandex.practicum.my_market_app.model.entity.Order;
import ru.yandex.practicum.my_market_app.repository.OrderItemRepository;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;
import ru.yandex.practicum.my_market_app.service.PaymentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
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
    private OrderItemRepository orderItemRepository;

    @Mock
    private ItemDao itemDao;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentService paymentService;

    @Test
    void getOrderDetail() {
        Order order = Order.builder().id(1L).totalSum(5L).build();
        OrderItemDto orderItemDto = new OrderItemDto(1L, "item1", 10, 5);

        List<OrderItemDto> orderItemsList = List.of(orderItemDto);

        when(orderRepository.findById(1L)).thenReturn(Mono.just(order));
        when(itemDao.getOrderItems(1L)).thenReturn(Flux.fromIterable(orderItemsList));

        OrderDetailDto orderDetailDto = orderService.getOrderDetail(1L).block();

        assertEquals(order.getId(), orderDetailDto.id());
        assertEquals(order.getTotalSum(), orderDetailDto.totalSum());
    }


    @Test
    void getOrders() {

        List<OrderItemDto> orderItems1 = List.of(new OrderItemDto(1L, "item1", 5L, 1));
        List<OrderItemDto> orderItems2 = List.of(
                new OrderItemDto(2L, "item2", 3L, 1),
                new OrderItemDto(3L, "item3", 7L, 1)
        );


        List<Order> orders = List.of(
                Order.builder().id(1L).totalSum(5L).build(),
                Order.builder().id(2L).totalSum(10L).build()
        );

        when(orderRepository.findAll()).thenReturn(Flux.fromIterable(orders));
        when(itemDao.getOrderItems(1L)).thenReturn(Flux.fromIterable(orderItems1));
        when(itemDao.getOrderItems(2L)).thenReturn(Flux.fromIterable(orderItems2));

        List<OrderDetailDto> orderDetailDtos = orderService.getOrders().collectList().block();

        assertEquals(orders.size(), orderDetailDtos.size());
        assertEquals(orders.getLast().getTotalSum(), orderDetailDtos.getLast().totalSum());
    }

    @Test
    void buy() {

        Order newOrder = Order.builder().totalSum(10L).build();
        Order savedOrder = Order.builder().id(1L).totalSum(10L).build();

        List<ItemFullDto> cartItems = List.of(
                new ItemFullDto(2L, "item2", "", "", 3L, 1),
                new ItemFullDto(3L, "item3", "", "", 7L, 1)
        );

        when(cartService.getCartItems()).thenReturn(Mono.just(cartItems));
        when(orderRepository.save(newOrder)).thenReturn(Mono.just(savedOrder));
        when(orderItemRepository.saveAll(anyCollection())).thenReturn(Flux.empty());
        when(paymentService.chargeOrderBalance(1L, 10L)).thenReturn(Mono.just(1L));
        when(cartService.clearCart()).thenReturn(Mono.empty());

        Long newId = orderService.buy().block();

        verify(orderRepository).save(newOrder);
        verify(orderItemRepository).saveAll(anyCollection());

        assertEquals(savedOrder.getId(), newId);
    }


}
