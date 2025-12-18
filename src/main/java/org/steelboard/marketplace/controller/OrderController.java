package org.steelboard.marketplace.controller;

import lombok.RequiredArgsConstructor; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.service.OrderService;

import java.security.Principal; 

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public String getOrderPage(@PathVariable Long orderId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Order order = orderService.getOrderById(orderId, principal.getName());

        model.addAttribute("order", order);
        return "order";
    }
}