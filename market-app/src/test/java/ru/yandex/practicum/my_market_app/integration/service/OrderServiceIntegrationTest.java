package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MyTestContainers;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderDetailDto;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("service")
@Testcontainers
@ImportTestcontainers(MyTestContainers.class)
@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DatabaseClient databaseClient;



    @BeforeEach
    void setUp() {
        databaseClient.sql("delete from cart").fetch().first().block();
        databaseClient.sql("insert into cart(id, item_id, count) values (1,1,5), (2,2,1)").fetch().first().block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_orders.sql"))).block();
    }

    @AfterEach
    void clearUp() {
        databaseClient.sql("delete from cart").fetch().first().block();
        databaseClient.sql("delete from orders").fetch().first().block();
        databaseClient.sql("alter table orders auto_increment = 1").fetch().first().block();
        databaseClient.sql("alter table cart auto_increment = 1").fetch().first().block();
    }


    @Test
    void getOrderDetail() {

        OrderDetailDto orderDetailDto = orderService.getOrderDetail(2L).block();

        assertEquals(2, orderDetailDto.items().size());
        assertEquals(15000L, orderDetailDto.totalSum());
    }

    @Test
    void getOrders() {

        List<OrderDetailDto> orderDetailDtoList = orderService.getOrders().collectList().block();

        assertEquals(2, orderDetailDtoList.size());
        assertEquals(1, orderDetailDtoList.getFirst().items().size());
        assertEquals(2, orderDetailDtoList.getLast().items().size());

    }

    @Test
    void buy() {

        Long newId = orderService.buy().block();
        OrderDetailDto orderDetailDto = orderService.getOrderDetail(newId).block();

        assertEquals(2, orderDetailDto.items().size());
        assertEquals(36000L, orderDetailDto.totalSum());
    }

}
