package org.steelboard.marketplace.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.OrderStatus;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.PickupPointRepository;
import org.steelboard.marketplace.service.OrderService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PickupPointRepository pickupPointRepository;

    // === НОВЫЙ МЕТОД: СПИСОК ВСЕХ ЗАКАЗОВ ===
    @GetMapping
    public String orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));

        // Вызываем метод сервиса (см. пункт 2)
        Page<Order> ordersPage = orderService.getAllOrders(search, pageable);

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "admin/order/orders"; // Шаблон списка заказов
    }

    // ... (Остальные методы: orderDetails, updateOrder, searchPickupPoints оставляем как были)
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/order/order_details";
    }

    @PostMapping("/{id}/update")
    public String updateOrder(@PathVariable Long id, @RequestParam OrderStatus status, @RequestParam(required = false) Long pickupPointId) {
        orderService.updateOrderDetails(id, status, pickupPointId);
        return "redirect:/admin/orders/" + id + "?success";
    }

    @GetMapping("/api/pickup-points")
    @ResponseBody
    public List<Map<String, Object>> searchPickupPoints(@RequestParam String city) {
        return pickupPointRepository.searchByCity(city).stream()
                .map(pp -> Map.of("id", (Object) pp.getId(), "address", pp.getAddress().toString()))
                .collect(Collectors.toList());
    }
}