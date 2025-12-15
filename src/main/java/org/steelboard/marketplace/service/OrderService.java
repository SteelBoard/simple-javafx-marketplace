package org.steelboard.marketplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.dto.order.OrderDto;
import org.steelboard.marketplace.entity.*;
import org.steelboard.marketplace.exception.OrderNotFoundException;
import org.steelboard.marketplace.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = false)
    public Order createOrder(User user, List<Long> productIds) {

        Cart cart = user.getCart();

        // Выбираем CartItems по productIds
        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(ci -> productIds.contains(ci.getProduct().getId()))
                .toList();

        if (selectedItems.isEmpty())
            throw new IllegalArgumentException("No selected items");

        // Создаём заказ
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);

        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        // Создаём OrderItems
        for (CartItem ci : selectedItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));

            total = total.add(oi.getUnitPrice());

            orderItemRepository.save(oi);
        }

        // Удаляем только выбранные cart items
        cartService.removeItemsFromCart(cart, selectedItems.stream().map(CartItem::getProduct).collect(Collectors.toList()));

        return order;
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public void updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        order.setStatus(status);
        orderRepository.save(order);
    }
}
