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
import ru.yandex.practicum.my_market_app.config.MyTestContainers;
import ru.yandex.practicum.my_market_app.model.dto.page.CartPageDto;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.service.CartService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@Tag("service")
@Testcontainers
@ImportTestcontainers(MyTestContainers.class)
@SpringBootTest
class CartServiceIntegrationTest {

    @Autowired
    private CartService cartService;

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
    }

    @AfterEach
    void clearUp() {
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_cart.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/reset_users.sql"))
        ).block();
        databaseClient.inConnection(connection -> ScriptUtils
                .executeSqlScript(connection, new ClassPathResource("db/scripts/clear_orders.sql"))).block();
    }

    @Test
    void getCart() {
        CartPageDto cartPageDto = cartService.getCart(1L).block();

        assertEquals(2, cartPageDto.itemsList().size());
        assertEquals(27000L, cartPageDto.totalSum());
    }

    @ParameterizedTest
    @CsvSource({"2,PLUS,38000", "1,MINUS,22000", "2,DELETE,5000"})
    void changeItemAmount(Long itemId, String action, Long totalSum) {
        cartService.changeItemAmount(itemId, action, 1L).block();
        CartPageDto cartPageDto = cartService.getCart(1L).block();

        assertEquals(totalSum, cartPageDto.totalSum());
    }

    @Test
    void getCartItemByItemId() {
        CartItem result = cartService.getCartItemByItemIdAndUserId(1L, 1L).block();

        assertEquals(1L, result.getItemId());
        assertEquals(1, result.getCount());
    }

    @Test
    void getCartItemsByIdList() {
        List<Long> itemIds = List.of(1L, 2L);

        List<CartItem> result = cartService.getCartItemsByIdList(itemIds, 1L).collectList().block();

        assertEquals(2, result.size());
    }

}
