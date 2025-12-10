package org.steelboard.marketplace.mapper;

import org.springframework.stereotype.Component;
import org.steelboard.marketplace.dto.cart.CartDto;
import org.steelboard.marketplace.dto.cart.CartItemDto;
import org.steelboard.marketplace.entity.Cart;

import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDto CartToDto(Cart cart) {
        var items = cart.getCartItems().stream()
                .map(item -> new CartItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getProduct().getMainImageUrl()
                ))
                .collect(Collectors.toList());
        return new CartDto(items, cart.getTotalPrice());
    }
}
