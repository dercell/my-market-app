package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MySqlContainer;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;
import java.util.Optional;

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
        Optional<ItemDto> item = itemService.getItem(4L);

        assertEquals("AT-ST", item.map(ItemDto::title).orElse(null));
    }

    @Test
    void changeItemAmount() {
        itemService.changeItemAmount(4L, "PLUS");

        Optional<ItemDto> item = itemService.getItem(4L);
        assertEquals(1, item.map(ItemDto::count).orElse(null));
    }

    @Test
    void getItemPage() {

        ItemPageDto itemPageDto = itemService.getItemsPage("", 0, 10, "NO");
        long itemCtn = itemPageDto.items().stream().mapToLong(List::size).sum();
        assertEquals(6, itemCtn);

    }

}
