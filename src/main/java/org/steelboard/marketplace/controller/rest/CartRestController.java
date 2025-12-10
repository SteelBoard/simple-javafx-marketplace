package org.steelboard.marketplace.controller.rest;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.steelboard.marketplace.dto.cart.CartDto;
import org.steelboard.marketplace.dto.cart.RemoveCartItemDto;
import org.steelboard.marketplace.dto.cart.UpdateCartItemDto;
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
                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (dto.getQuantity() < 1) dto.setQuantity(1);
        cartService.updateQuantity(userDetails.getUsername(), dto.getProductId(), dto.getQuantity());
        return cartMapper.CartToDto(cartService.getCartByUsername(userDetails.getUsername()));
    }

    @PostMapping("/remove")
    public CartDto removeCartItem(@RequestBody RemoveCartItemDto dto,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        cartService.removeItem(userDetails.getUsername(), dto.getProductId());
        return cartMapper.CartToDto(cartService.getCartByUsername(userDetails.getUsername()));
    }

    @PostMapping("/clear")
    public CartDto clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCartByUsername(userDetails.getUsername());
        return cartMapper.CartToDto(cartService.getCartByUsername(userDetails.getUsername()));
    }
}
