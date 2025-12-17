package org.steelboard.marketplace.controller;

import lombok.RequiredArgsConstructor; // Лучше использовать это вместо AllArgsConstructor
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.service.OrderService;

import java.security.Principal; // <-- Важный импорт

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public String getOrderPage(@PathVariable Long orderId, Model model, Principal principal) {
        // Если пользователь не вошел, Spring Security обычно перекинет на логин,
        // но проверка на null не помешает
        if (principal == null) {
            return "redirect:/login";
        }

        // Передаем ID заказа и email текущего пользователя
        Order order = orderService.getOrderById(orderId, principal.getName());

        model.addAttribute("order", order);
        return "order";
    }
}