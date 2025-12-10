package org.steelboard.marketplace.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.service.CartService;

@AllArgsConstructor
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String cartPage(Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        var cart = cartService.getCartByUsername(userDetails.getUsername());
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        return "cart";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.addProductToCart(id, userDetails.getUsername());
        return "redirect:/cart"; // после добавления перенаправляем на страницу корзины
    }
}
