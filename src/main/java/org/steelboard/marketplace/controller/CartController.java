package org.steelboard.marketplace.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.CartService;
import org.steelboard.marketplace.service.UserService;

@AllArgsConstructor
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @GetMapping
    public String cartPage(Model model,
                           @AuthenticationPrincipal User userDetails) {
        Cart cart = userService.findById(userDetails.getId()).getCart();
        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        return "cart";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id,
                            @AuthenticationPrincipal User userDetails) {
        User user = userService.findById(userDetails.getId());
        cartService.addProductToCart(id, user);
        return "redirect:/cart"; 
    }
}
