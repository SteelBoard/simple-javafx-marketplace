package org.steelboard.marketplace.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.steelboard.marketplace.dto.cart.CartDto;
import org.steelboard.marketplace.dto.cart.RemoveCartItemDto;
import org.steelboard.marketplace.dto.cart.UpdateCartItemDto;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.mapper.CartMapper;
import org.steelboard.marketplace.service.CartService;

@RestController
@RequestMapping("/api/cart")
@AllArgsConstructor
public class CartRestController {

    private final CartService cartService;
    private CartMapper cartMapper;

    @PostMapping("/update")
    public CartDto updateCartItem(@RequestBody UpdateCartItemDto dto,
                                  @AuthenticationPrincipal User user) {
        if (dto.getQuantity() < 1) dto.setQuantity(1);
        cartService.updateQuantity(user, dto.getProductId(), dto.getQuantity());
        return cartMapper.CartToDto(user.getCart());
    }

    @PostMapping("/remove")
    public CartDto removeCartItem(@RequestBody RemoveCartItemDto dto,
                                  @AuthenticationPrincipal User user) {
        cartService.removeItem(user, dto.getProductId());
        return cartMapper.CartToDto(user.getCart());
    }

    @PostMapping("/clear")
    public CartDto clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCartByUser(user);
        return cartMapper.CartToDto(user.getCart());
    }
}
