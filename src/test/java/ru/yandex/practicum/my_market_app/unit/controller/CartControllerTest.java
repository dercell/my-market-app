package ru.yandex.practicum.my_market_app.unit.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.my_market_app.controller.CartController;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.service.CartService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("controller")
@Tag("unit")
@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    private CartPageDto cart;

    @BeforeEach
    public void setUp() {
        cart = new CartPageDto(
                List.of(new ItemDto(1L, "item1", "", "", 10, 5),
                        new ItemDto(2L, "item2", "", "", 3, 1)),
                53L
        );
    }

    @Test
    void getCartItems() throws Exception {

        when(cartService.getCart()).thenReturn(cart);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", cart.itemsList()))
                .andExpect(model().attribute("total", cart.totalSum()));
    }

    @Test
    void changeItemAmount() throws Exception {

        when(cartService.changeItemAmount(2L, "PLUS")).thenReturn(cart);

        mockMvc.perform(post("/cart/items")
                        .param("id", "2")
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", cart.itemsList()))
                .andExpect(model().attribute("total", cart.totalSum()));
    }

    @Test
    void buy() throws Exception {

        when(cartService.buy()).thenReturn(3L);

        mockMvc.perform(post("/cart/items/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/orders/3?newOrder=true"));
    }

    @Test
    void buyError() throws Exception {

        when(cartService.buy()).thenThrow(new RuntimeException("Save error!"));

        mockMvc.perform(post("/cart/items/buy"))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("message", "Save error!"));
    }

}
