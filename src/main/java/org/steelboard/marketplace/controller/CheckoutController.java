package org.steelboard.marketplace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.OrderService;
import org.steelboard.marketplace.service.PickupPointService;
import org.steelboard.marketplace.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final UserService userService;
    private final OrderService orderService;
    private final PickupPointService pickupPointService;

    @GetMapping("/checkout")
    public String checkout(
            @AuthenticationPrincipal User userDetails,
            Model model
    ) {
        User user = userService.findById(userDetails.getId());
        Cart cart = user.getCart();

        model.addAttribute("cartItems", cart.getCartItems());
        model.addAttribute("pickupPoints", pickupPointService.findAll());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String submitCheckout(
            @AuthenticationPrincipal User userDetails,
            @RequestParam(required = false) List<Long> selectedProductIds,
            @RequestParam Long pickupPointId
    ) {
        if (selectedProductIds == null || selectedProductIds.isEmpty()) {
            
            return "redirect:/checkout?error=noProductsSelected";
        }

        User user = userService.findById(userDetails.getId());
        Order order = orderService.createOrder(user, selectedProductIds, pickupPointId);
        return "redirect:/order/" + order.getId();
    }

}
