package org.steelboard.marketplace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.CartService;
import org.steelboard.marketplace.service.UserService;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final UserService userService;

    @GetMapping("/checkout")
    public String checkout(
            @AuthenticationPrincipal User userDetails,
            Model model
    ) {
        User user = userService.findById(userDetails.getId());
        Cart cart = user.getCart();
        model.addAttribute("cartItems", cart.getCartItems());
        return "checkout";
    }
}
