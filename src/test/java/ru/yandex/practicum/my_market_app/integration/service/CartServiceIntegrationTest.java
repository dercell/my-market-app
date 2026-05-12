package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.service.CartService;
import ru.yandex.practicum.my_market_app.service.OrderService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("service")
@Transactional
@Sql(statements = "insert into cart(id, item_id, count) values (1,1,1), (2,2,2)", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(statements = "truncate table cart", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Sql(value = "classpath:db/scripts/clear_orders.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@SpringBootTest
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Test
    void getCart() {
        CartPageDto cartPageDto = cartService.getCart();

        assertEquals(2, cartPageDto.itemsList().size());
        assertEquals(27000L, cartPageDto.totalSum());
    }

    @ParameterizedTest
    @CsvSource({"2,PLUS,38000", "1,MINUS,22000", "2,DELETE,5000"})
    void changeItemAmount(Long itemId, String action, Long totalSum) {
        cartService.changeItemAmount(itemId, action);
        CartPageDto cartPageDto = cartService.getCart();

        assertEquals(totalSum, cartPageDto.totalSum());
    }

    @Test
    void buy() {
        cartService.buy();

        OrderPageDto orderPageDto = orderService.getOrderDetail(1L);
        assertEquals(27000L, orderPageDto.totalSum());
    }

}
