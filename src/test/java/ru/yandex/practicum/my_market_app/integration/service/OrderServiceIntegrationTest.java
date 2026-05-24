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

        OrderPageDto orderPageDto = orderService.getOrderDetail(2L).block();

        assertEquals(2, orderPageDto.items().size());
        assertEquals(15000L, orderPageDto.totalSum());
    }

    @Test
    void getOrders() {

        List<OrderPageDto> orderPageDtoList = orderService.getOrders().collectList().block();

        assertEquals(2, orderPageDtoList.size());
        assertEquals(1, orderPageDtoList.getFirst().items().size());
        assertEquals(2, orderPageDtoList.getLast().items().size());

    }

    @Test
    void buy() {

        OrderPageDto orderPageDto = orderService.buy().flatMap(orderId -> orderService.getOrderDetail(orderId))
                .block();

        assertEquals(2, orderPageDto.items().size());
        assertEquals(36000L, orderPageDto.totalSum());
    }

}
