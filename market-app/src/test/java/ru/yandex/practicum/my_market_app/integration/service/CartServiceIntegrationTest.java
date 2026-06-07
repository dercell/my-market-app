package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import org.springframework.r2dbc.core.DatabaseClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.page.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.OrderDetailDto;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("service")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@SpringBootTest
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        databaseClient.sql("insert into cart(id, item_id, count) values (1,1,1), (2,2,2)").fetch().first().block();
    }

    @AfterEach
    void clearUp() {
        databaseClient.sql("delete from cart").fetch().first().block();
        databaseClient.sql("alter table cart auto_increment = 1").fetch().first().block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/clear_orders.sql"))).block();
    }

    @Test
    void getCart() {
        CartPageDto cartPageDto = cartService.getCart().block();

        assertEquals(2, cartPageDto.itemsList().size());
        assertEquals(27000L, cartPageDto.totalSum());
    }

    @ParameterizedTest
    @CsvSource({"2,PLUS,38000", "1,MINUS,22000", "2,DELETE,5000"})
    void changeItemAmount(Long itemId, String action, Long totalSum) {
        cartService.changeItemAmount(itemId, action).block();
        CartPageDto cartPageDto = cartService.getCart().block();

        assertEquals(totalSum, cartPageDto.totalSum());
    }

    @Test
    void buy() {
        cartService.buy().block();

        OrderDetailDto orderDetailDto = orderService.getOrderDetail(1L).block();
        assertEquals(27000L, orderDetailDto.totalSum());
    }

}
