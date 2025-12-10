package org.steelboard.marketplace.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.steelboard.marketplace.dto.cart.CartDto;
import org.steelboard.marketplace.dto.cart.CartItemDto;
import org.steelboard.marketplace.dto.cart.RemoveCartItemDto;
import org.steelboard.marketplace.dto.cart.UpdateCartItemDto;
import org.steelboard.marketplace.service.CartService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartRestController {

    private final CartService cartService;

    private CartDto mapToDto(String username) {
        var cart = cartService.getCartByUsername(username);
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

    @PostMapping("/update")
    public CartDto updateCartItem(@RequestBody UpdateCartItemDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (dto.getQuantity() < 1) dto.setQuantity(1);
        cartService.updateQuantity(userDetails.getUsername(), dto.getProductId(), dto.getQuantity());
        return mapToDto(userDetails.getUsername());
    }

    @PostMapping("/remove")
    public CartDto removeCartItem(@RequestBody RemoveCartItemDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        cartService.removeItem(userDetails.getUsername(), dto.getProductId());
        return mapToDto(userDetails.getUsername());
    }
}
