package org.steelboard.marketplace.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.mapper.OrderMapper;
import org.steelboard.marketplace.service.OrderService;

@Controller
@RequestMapping("/order")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    // Страница просмотра заказа по ID
    @GetMapping("/{orderId}")
    public String getOrderPage(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId); // метод в сервисе, который ищет заказ по ID
        model.addAttribute("order", orderMapper.toOrderDto(order));
        return "order"; // имя Thymeleaf шаблона order.html
    }
}
