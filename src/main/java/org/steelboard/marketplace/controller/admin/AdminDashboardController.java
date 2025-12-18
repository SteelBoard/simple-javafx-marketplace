package org.steelboard.marketplace.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.ProductRepository;
import org.steelboard.marketplace.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminDashboardController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public String dashboard(Model model) {
        // 1. Статистика карточками
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        BigDecimal revenueToday = orderRepository.sumTotalToday();
        Long ordersToday = orderRepository.countOrdersToday();

        // 2. Топ товаров
        List<Product> topProducts = productRepository.findTop5BestSellers();

        // 3. Данные для графика (Мапим Object[] в списки для JS)
        List<Object[]> chartData = orderRepository.getRevenueLast7Days();
        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartValues = new ArrayList<>();

        for (Object[] row : chartData) {
            chartLabels.add(row[0].toString()); // Дата
            chartValues.add((BigDecimal) row[1]); // Сумма
        }

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("revenueToday", revenueToday);
        model.addAttribute("ordersToday", ordersToday);
        model.addAttribute("topProducts", topProducts);

        // Для подсветки активного меню
        model.addAttribute("activeTab", "dashboard");

        // Данные для JS
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);

        return "admin/admin";
    }
}