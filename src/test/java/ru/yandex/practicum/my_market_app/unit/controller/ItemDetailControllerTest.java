package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.my_market_app.controller.ItemDetailController;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("view")
@Tag("unit")
@WebMvcTest(ItemDetailController.class)
class ItemDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getItem() throws Exception {

        Optional<ItemDto> itemDto = Optional.of(new ItemDto(1L, "item1", "", "", 5L, 1));

        when(itemService.getItem(1L)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attribute("item", itemDto.get()));

    }

    @Test
    void changeItemAmount() throws Exception {

        Optional<ItemDto> itemDto = Optional.of(new ItemDto(1L, "item1", "", "", 5L, 1));

        when(itemService.getItem(1L)).thenReturn(itemDto);
        doNothing().when(itemService).changeItemAmount(1L, "PLUS");

        mockMvc.perform(get("/items/{id}", 1L)
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attribute("item", itemDto.get()));

    }

}
