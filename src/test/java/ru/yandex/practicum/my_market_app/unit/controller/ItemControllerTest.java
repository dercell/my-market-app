package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.my_market_app.controller.ItemController;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemPageDto;
import ru.yandex.practicum.my_market_app.model.dto.PageDto;
import ru.yandex.practicum.my_market_app.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("controller")
@Tag("unit")
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getItemPage() throws Exception {

        ItemPageDto itemPageDto = new ItemPageDto(
                List.of(List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                        new ItemDto(2L, "item2", "", "", 3, 1))),
                "",
                "NO",
                new PageDto(0, 5, false, false)
        );
        when(itemService.getItemsPage("", 0, 5, "NO")).thenReturn(itemPageDto);

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items"))
                .andExpect(model().attribute("items", itemPageDto.items()))
                .andExpect(model().attribute("search", itemPageDto.search()))
                .andExpect(model().attribute("sort", itemPageDto.sort()))
                .andExpect(model().attribute("paging", itemPageDto.paging()));

    }

    @Test
    void changeItemAmount() throws Exception {

        doNothing().when(itemService).changeItemAmount(anyLong(), anyString());

        mockMvc.perform(post("/items")
                        .param("id", "1")
                        .param("search", "")
                        .param("pageNumber", "0")
                        .param("pageSize", "5")
                        .param("sort", "NO")
                        .param("action", "PLUS")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/items?search=&sort=NO&pageNumber=0&pageSize=5"));

    }

}
