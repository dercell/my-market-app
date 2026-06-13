package ru.yandex.practicum.my_market_app.integration.service;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.my_market_app.config.MyTestContainers;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemFullDto;
import ru.yandex.practicum.my_market_app.model.dto.detail.ItemInfoDto;
import ru.yandex.practicum.my_market_app.model.dto.page.ItemPageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("integration")
@Tag("service")
@Testcontainers
@ImportTestcontainers(MyTestContainers.class)
@SpringBootTest
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    ReactiveRedisTemplate<String, ItemInfoDto> redisTemplate;

    @Test
    void getItemFromDb() {
        ItemFullDto itemFullDto = itemService.getItem(4L).block();

        assertEquals("AT-ST", itemFullDto.getTitle());
    }

    @Test
    void getItemFromCache() {
        redisTemplate.opsForValue().set("itemInfo:4",
                        new ItemInfoDto(4L,
                                "AT-ST",
                                "Лего набор \"Шагоход AT-ST\"",
                                "atst.jpg",
                                3000L))
                .block();

        ItemFullDto itemFullDto = itemService.getItem(4L).block();

        assertEquals("AT-ST", itemFullDto.getTitle());
    }


    @Test
    void changeItemAmount() {
        itemService.changeItemAmount(4L, "PLUS").block();

        ItemFullDto item = itemService.getItem(4L).block();
        assertEquals(1, item.getCount());
    }

    @Test
    void getItemPage() {

        ItemPageDto itemPageDto = itemService.getItemsPage("", 0, 10, "NO").block();
        long itemCtn = itemPageDto.items().stream().mapToLong(List::size).sum();
        assertEquals(6, itemCtn);

    }

    @Test
    void getItemPagePartialCache() {
        redisTemplate.opsForValue().set("itemInfo:1",
                        new ItemInfoDto(1L,
                                "X-Wing",
                                "Лего набор \"X-Wing\"",
                                "xwing.jpg",
                                5000L))
                .block();

        redisTemplate.opsForValue().set("itemInfo:4",
                        new ItemInfoDto(4L,
                                "AT-ST",
                                "Лего набор \"Шагоход AT-ST\"",
                                "atst.jpg",
                                3000L))
                .block();

        ItemPageDto itemPageDto = itemService.getItemsPage("", 0, 10, "NO").block();
        long itemCtn = itemPageDto.items().stream().mapToLong(List::size).sum();
        assertEquals(6, itemCtn);

    }

}
