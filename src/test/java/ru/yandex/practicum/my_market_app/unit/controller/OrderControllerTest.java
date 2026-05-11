package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.my_market_app.controller.OrderController;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.dto.OrderPageDto;
import ru.yandex.practicum.my_market_app.service.OrderService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("view")
@Tag("unit")
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void getOrders() throws Exception {

        List<OrderPageDto> orderPageDtoList = List.of(
                new OrderPageDto(
                        1L,
                        List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                                new ItemDto(2L, "item2", "", "", 3, 1)),
                        53L
                )
        );
        when(orderService.getOrders()).thenReturn(orderPageDtoList);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attribute("orders", orderPageDtoList));

    }

    @Test
    void getOrderDetail() throws Exception {
        OrderPageDto orderPageDto = new OrderPageDto(
                1L,
                List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                        new ItemDto(2L, "item2", "", "", 3, 1)),
                53L
        );
        when(orderService.getOrderDetail(1L)).thenReturn(orderPageDto);


        mockMvc.perform(get("/orders/{id}", 1L)
                        .param("newOrder", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"));

    }


}
