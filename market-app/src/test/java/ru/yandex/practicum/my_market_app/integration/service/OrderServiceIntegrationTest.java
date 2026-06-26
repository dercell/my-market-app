package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.server.ServerWebExchange;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
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
@AutoConfigureWebTestClient
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DatabaseClient databaseClient;


    @BeforeEach
    void setUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_users.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_cart.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/init_orders.sql"))).block();
    }

    @AfterEach
    void clearUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_orders.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_cart.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_users.sql"))).block();
    }


    @Test
    void getOrderDetail() {

        OrderDetailDto orderDetailDto = orderService.getOrderDetail(2L, 1L).block();

        assertEquals(2, orderDetailDto.items().size());
        assertEquals(15000L, orderDetailDto.totalSum());
    }

    @Test
    void getOrders() {

        List<OrderDetailDto> orderDetailDtoList = orderService.getOrders(1L).collectList().block();

        assertEquals(2, orderDetailDtoList.size());
        assertEquals(1, orderDetailDtoList.stream().filter(det -> det.id() == 1L).map(OrderDetailDto::items).map(List::size).findFirst().orElse(0));
        assertEquals(2,orderDetailDtoList.stream().filter(det -> det.id() == 2L).map(OrderDetailDto::items).map(List::size).findFirst().orElse(0));

    }

    @Test
    void buy() {

        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/chargeBalance").build()
        );

        StepVerifier.create(
                orderService.buy(1L)
                        .contextWrite(context -> context.put(ServerWebExchange.class, exchange))
                        .flatMap(newId -> orderService.getOrderDetail(newId, 1L))
        ).assertNext(orderDetailDto -> {
            assertEquals(2, orderDetailDto.items().size());
            assertEquals(27000L, orderDetailDto.totalSum());
        }).verifyComplete();
    }

}
