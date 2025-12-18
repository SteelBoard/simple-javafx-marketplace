package org.steelboard.marketplace.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.OrderStatus;
import org.steelboard.marketplace.entity.PickupPoint;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.PickupPointRepository;
import org.steelboard.marketplace.service.OrderService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PickupPointRepository pickupPointRepository;

    
    @GetMapping
    public String orders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, mapSortField(sort)));

        Page<Order> ordersPage;

        if (search != null && !search.isBlank()) {
            String term = search.trim();

            if (term.startsWith("product:")) {
                
                try {
                    Long productId = Long.parseLong(term.split(":")[1]);
                    
                    ordersPage = orderRepository.findOrdersByProductId(productId, pageable);
                } catch (NumberFormatException e) {
                    
                    ordersPage = Page.empty();
                }

            } else if (term.startsWith("pvz:")) {
                
                try {
                    Long pvzId = Long.parseLong(term.split(":")[1]);
                    ordersPage = orderRepository.findOrdersByPvzId(pvzId, pageable);
                } catch (NumberFormatException e) {
                    ordersPage = Page.empty();
                }

            } else {
                
                
                ordersPage = orderService.findByUsernameOrStatus(term, pageable);
            }
        } else {
            
            ordersPage = orderService.findAll(pageable); 
        }

        model.addAttribute("ordersPage", ordersPage);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);

        return "admin/order/orders";
    }

    
    @GetMapping("/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));

        
        List<PickupPoint> pickupPoints = pickupPointRepository.findAllWithAddress(
                Sort.by("address.city", "address.street", "address.houseNumber")
        );

        model.addAttribute("order", order);
        model.addAttribute("pickupPoints", pickupPoints);

        return "admin/order/order_details";
    }

    
    @PostMapping("/{id}/update")
    public String updateOrder(
            @PathVariable Long id,
            @RequestParam("status") OrderStatus status,
            @RequestParam(value = "pickupPointId", required = false) Long pickupPointId
    ) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ не найден"));

        
        order.setStatus(status);

        
        if (pickupPointId != null) {
            PickupPoint pp = pickupPointRepository.findById(pickupPointId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "ПВЗ не найден"));
            order.setPickupPoint(pp);
        } else {
            
            order.setPickupPoint(null);
        }

        orderRepository.save(order);

        return "redirect:/admin/orders/" + id;
    }

    
    private String mapSortField(String sort) {
        return switch (sort) {
            case "user" -> "user.username";
            case "status" -> "status";
            case "totalAmount" -> "totalAmount";
            case "createdAt" -> "createdAt";
            default -> "id";
        };
    }
}