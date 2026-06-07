package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemDetailDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("integration")
@Tag("service")
@Testcontainers
@ImportTestcontainers(MySqlContainer.class)
@SpringBootTest
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;


    @Test
    void getItem() {
        ItemDetailDto itemDetailDto = itemService.getItem(4L).block();

        assertEquals("AT-ST", itemDetailDto.title());
    }

    @Test
    void changeItemAmount() {
        itemService.changeItemAmount(4L, "PLUS").block();

        ItemDetailDto item = itemService.getItem(4L).block();
        assertEquals(1, item.count());
    }

    @Test
    void getItemPage() {

        ItemPageDto itemPageDto = itemService.getItemsPage("", 0, 10, "NO").block();
        long itemCtn = itemPageDto.items().stream().mapToLong(List::size).sum();
        assertEquals(6, itemCtn);

    }

}
