package ru.yandex.practicum.my_market_app.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.model.entity.Order;
import ru.yandex.practicum.my_market_app.model.entity.OrderItems;
import ru.yandex.practicum.my_market_app.repository.OrderRepository;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void getOrderDetail() {
        Optional<Order> orderOptional = Optional.of(
                Order.builder().id(1L).totalSum(5L).orderItems(
                        List.of(OrderItems.builder().id(1L).item(
                                Item.builder().id(1L).title("item1").description("").price(5L).imgPath("").build()
                        ).count(1).build())
                ).build()
        );

        when(orderRepository.findById(1L)).thenReturn(orderOptional);

        OrderPageDto orderPageDto = orderService.getOrderDetail(1L);

        assertEquals(orderOptional.map(Order::getId).orElse(null), orderPageDto.id());
        assertEquals(orderOptional.map(Order::getOrderItems).map(List::size).orElse(null), orderPageDto.items().size());
        assertEquals(orderOptional.map(Order::getTotalSum).orElse(null), orderPageDto.totalSum());
    }

    @Test
    void getOrders() {
        List<Order> orders = List.of(
                Order.builder().id(1L).totalSum(5L).orderItems(
                        List.of(OrderItems.builder().id(1L).item(
                                Item.builder().id(1L).title("item1").description("").price(5L).imgPath("").build()
                        ).count(1).build())
                ).build(),
                Order.builder().id(2L).totalSum(10L).orderItems(
                        List.of(OrderItems.builder().id(1L).item(
                                        Item.builder().id(2L).title("item2").description("").price(3L).imgPath("").build()
                                ).count(1).build(),
                                OrderItems.builder().id(3L).item(
                                        Item.builder().id(3L).title("item3").description("").price(7L).imgPath("").build()
                                ).count(1).build())
                ).build()
        );

        when(orderRepository.findAll()).thenReturn(orders);

        List<OrderPageDto> orderPageDtos = orderService.getOrders();

        assertEquals(orders.size(), orderPageDtos.size());
        assertEquals(orders.getLast().getTotalSum(), orderPageDtos.getLast().totalSum());
    }

    @Test
    void buy() {

        Order newOrder = Order.builder().id(1L).totalSum(5L).orderItems(
                List.of(OrderItems.builder().id(1L).item(
                                Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()
                        ).count(1).build(),
                        OrderItems.builder().id(1L).item(
                                Item.builder().id(2L).title("item2").description("").price(4L).imgPath("").build()
                        ).count(1).build()
                )
        ).build();

        List<CartItem> cartItemList = List.of(
                CartItem.builder().id(1L).item(Item.builder().id(1L).title("item1").description("").price(3L).imgPath("").build()).count(1).build(),
                CartItem.builder().id(2L).item(Item.builder().id(2L).title("item2").description("").price(4L).imgPath("").build()).count(5).build()
        );

        when(orderRepository.save(any(Order.class))).thenReturn(newOrder);

        Long newId = orderService.buy(cartItemList);
        verify(orderRepository).save(any(Order.class));

        assertEquals(newOrder.getId(), newId);
    }

}
