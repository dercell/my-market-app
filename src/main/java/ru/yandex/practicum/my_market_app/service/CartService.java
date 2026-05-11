package ru.yandex.practicum.my_market_app.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.my_market_app.model.entity.CartItem;
import ru.yandex.practicum.my_market_app.model.dto.CartPageDto;
import ru.yandex.practicum.my_market_app.model.dto.ItemDto;
import ru.yandex.practicum.my_market_app.model.entity.Item;
import ru.yandex.practicum.my_market_app.repository.CartRepository;
import ru.yandex.practicum.my_market_app.repository.ItemRepository;
import ru.yandex.practicum.my_market_app.util.mappers.ItemMapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    public CartPageDto getCart() {
        AtomicLong totalSum = new AtomicLong(0L);

        List<CartItem> cartItems = cartRepository.findAll();

        List<ItemDto> cartItemDtos = cartItems.stream().map(cartItem -> {
            totalSum.addAndGet(cartItem.getItem().getPrice() * cartItem.getCount());
            return ItemMapper.toDto(cartItem.getItem(), cartItem.getCount());
        }).toList();

        return new CartPageDto(cartItemDtos, totalSum.get());
    }

    @Transactional
    public CartPageDto changeItemAmount(Long itemId, String action) {
        List<CartItem> cartItemList = cartRepository.getCartItemByItem_Id(itemId);

        if (cartItemList.isEmpty()) {
            if ("PLUS".equalsIgnoreCase(action)) {
                Item item = itemRepository.getReferenceById(itemId);
                CartItem newItem = CartItem.builder().item(item).count(1).build();
                cartRepository.save(newItem);
            }
        } else {
            CartItem cartItem = cartItemList.getFirst();
            if ("PLUS".equalsIgnoreCase(action)) {
                cartItem.addOne();
                cartRepository.save(cartItem);
            } else if ("MINUS".equalsIgnoreCase(action) && cartItem.getCount() > 1) {
                cartItem.delOne();
                cartRepository.save(cartItem);
            } else {
                cartItem.getItem().setCartItem(null);
                cartItem.setItem(null);
                cartRepository.delete(cartItem);
            }

        }

        return this.getCart();
    }

    @Transactional
    public Long buy() {
        List<CartItem> cartItems = cartRepository.findAll();
        Long newOrderId = orderService.buy(cartItems);

        cartItems.forEach(cartItem -> {
            cartItem.getItem().setCartItem(null);
            cartItem.setItem(null);
        });

        cartRepository.deleteAll();
        return newOrderId;
    }
}
