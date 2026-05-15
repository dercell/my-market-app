package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("service")
@Transactional
@Sql(value = "classpath:db/scripts/init_orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(statements = "delete from orders", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(statements = "alter table orders auto_increment = 1", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;


    @Test
    void getOrderDetail() {

        OrderPageDto orderPageDto = orderService.getOrderDetail(2L);

        assertEquals(2, orderPageDto.items().size());
        assertEquals(15000L, orderPageDto.totalSum());

    }

    @Test
    void getOrders() {

        List<OrderPageDto> orderPageDtoList = orderService.getOrders();

        assertEquals(2, orderPageDtoList.size());
        assertEquals(1, orderPageDtoList.getFirst().items().size());
        assertEquals(2, orderPageDtoList.getLast().items().size());

    }

    @Test
    void buy() {

        List<CartItem> cartItemList = List.of(
                CartItem.builder().id(1L).item(Item.builder().id(1L).title("X-Wing").description("Лего набор \"X-Wing\"").price(5000L).imgPath("xwing.jpg").build()).count(5).build(),
                CartItem.builder().id(2L).item(Item.builder().id(2L).title("Venator").description("Лего набор \"Крейсер Venator\"").price(11000L).imgPath("venator.jpg").build()).count(1).build()
        );

        orderService.buy(cartItemList);
        OrderPageDto orderPageDto = orderService.getOrderDetail(3L);

        assertEquals(2, orderPageDto.items().size());
        assertEquals(36000L, orderPageDto.totalSum());
    }

}
