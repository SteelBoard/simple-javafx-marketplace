package org.steelboard.marketplace.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.dto.product.ProductStatDto;
import org.steelboard.marketplace.repository.OrderItemRepository;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    @GetMapping
    public String adminDashboard(Model model) {
        
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        Date startOfDay = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long ordersToday = orderRepository.countByCreatedAtAfter(startOfDay);
        model.addAttribute("ordersToday", ordersToday);
        model.addAttribute("totalUsers", userRepository.count());

        List<ProductStatDto> topProducts = orderItemRepository.findTopSellingProducts(PageRequest.of(0, 5));
        model.addAttribute("topProducts", topProducts);

        return "admin/admin"; 
    }
}