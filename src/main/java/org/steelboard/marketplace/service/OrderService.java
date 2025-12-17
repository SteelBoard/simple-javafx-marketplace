package org.steelboard.marketplace.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.*;
import org.steelboard.marketplace.exception.OrderNotFoundException;
import org.steelboard.marketplace.repository.OrderItemRepository;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.PickupPointRepository;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final PickupPointRepository pickupPointRepository;
    private final PaymentService paymentService;

    // 🔥 1. Внедряем ProductService
    private final ProductService productService;

    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }

    public boolean hasUserBoughtProduct(Long userId, Long productId) {
        return orderItemRepository.existsByOrder_User_IdAndProduct_IdAndOrder_Status(
                userId, productId, OrderStatus.DELIVERED
        );
    }

    @SneakyThrows
    public Order getOrderById(Long id, String currentUsername) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // 🔥 ПРОВЕРКА: "Свой-Чужой"
        // Сравниваем email владельца заказа с тем, кто зашел
        // (Предполагаю, что логин у тебя идет по email. Если нет - используй .getUsername())
        if (!order.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Вы не можете просматривать чужие заказы");
        }

        return order;
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> findByUsernameOrStatus(String search, Pageable pageable) {
        return orderRepository.findBySearch(search, pageable);
    }


    @Transactional(readOnly = false)
    public Order createOrder(
            User user,
            List<Long> productIds,
            Long pickupPointId
    ) {

        Cart cart = user.getCart();

        PickupPoint pickupPoint = pickupPointRepository
                .findById(pickupPointId)
                .orElseThrow(() -> new IllegalArgumentException("Pickup point not found"));

        List<CartItem> selectedItems = cart.getCartItems().stream()
                .filter(ci -> productIds.contains(ci.getProduct().getId()))
                .toList();

        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException("No selected items");
        }

        BigDecimal total = selectedItems.stream()
                .map(CartItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==== ОПЛАТА ====
        paymentService.pay(total);

        // ==== СОЗДАНИЕ ЗАКАЗА ====
        Order order = new Order();
        order.setUser(user);
        order.setPickupPoint(pickupPoint);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);

        order = orderRepository.save(order);

        for (CartItem ci : selectedItems) {

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getUnitPrice());

            orderItemRepository.save(oi);

            // 🔥 2. Увеличиваем счетчик продаж
            // Берем ID товара и кол-во из корзины
            productService.incrementProductSales(
                    ci.getProduct().getId(),
                    ci.getQuantity()
            );
        }

        cartService.removeItemsFromCart(
                cart,
                selectedItems.stream().map(CartItem::getProduct).toList()
        );

        return order;
    }
}