package org.steelboard.marketplace.service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.dto.order.SellerStatsDto;
import org.steelboard.marketplace.dto.product.ProductStatDto;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.CartItem;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.OrderItem;
import org.steelboard.marketplace.entity.OrderItemStatus;
import org.steelboard.marketplace.entity.OrderStatus;
import org.steelboard.marketplace.entity.PickupPoint;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.exception.OrderNotFoundException;
import org.steelboard.marketplace.repository.OrderItemRepository;
import org.steelboard.marketplace.repository.OrderRepository;
import org.steelboard.marketplace.repository.PickupPointRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final PickupPointRepository pickupPointRepository;
    private final PaymentService paymentService;
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

        if (!order.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Вы не можете просматривать чужие заказы");
        }

        return order;
    }

    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> getUserOrders(Long userId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return orderRepository.findByUserIdAndSearch(userId, search.trim(), pageable);
        }
        return orderRepository.findByUser_Id(userId, pageable);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
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

        
        paymentService.pay(total);

        
        Order order = new Order();
        order.setUser(user);
        order.setPickupPoint(pickupPoint);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PROCESSING);

        order = orderRepository.save(order);

        for (CartItem ci : selectedItems) {

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(ci.getUnitPrice());
            oi.setStatus(OrderItemStatus.PROCESSING);

            orderItemRepository.save(oi);

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

    public Page<Order> findOrdersByProductId(Long id, Pageable pageable) {
        return orderRepository.findOrdersByProductId(id, pageable);
    }

    public Page<Order> getAllOrders(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return orderRepository.findAllBySearch(search.trim(), pageable);
        }
        return orderRepository.findAll(pageable);
    }


    @Transactional
    public void updateOrderDetails(Long orderId, OrderStatus status, Long pickupPointId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (status != null) {
        }

        if (pickupPointId != null) {
            PickupPoint pp = pickupPointRepository.findById(pickupPointId)
                    .orElseThrow(() -> new RuntimeException("Pickup point not found"));
            order.setPickupPoint(pp);
        }

        recomputeOrderStatusFromItems(order);

        orderRepository.save(order);
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> findOrdersBySellerId(Long sellerId) {
        return orderRepository.findOrdersBySellerId(sellerId);
    }

    @Transactional
    public void changeStatus(Long orderId, User currentUser, String newStatusStr) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(newStatusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный статус");
        }

        boolean isBuyer = order.getUser().getId().equals(currentUser.getId());
        boolean isSeller = order.getOrderItems().stream()
                .anyMatch(item -> item.getProduct().getSeller().getId().equals(currentUser.getId()));

       if (isSeller && newStatus == OrderStatus.SHIPPED) {
            List<OrderItem> toShip = order.getOrderItems().stream()
                    .filter(i -> i.getProduct().getSeller().getId().equals(currentUser.getId()))
                    .filter(i -> i.getStatus() == OrderItemStatus.CONFIRMED)
                    .toList();
            if (toShip.isEmpty()) {
                throw new IllegalStateException("Нет подтвержденных позиций для отправки");
            }
            toShip.forEach(i -> i.setStatus(OrderItemStatus.SHIPPED));
            orderItemRepository.saveAll(toShip);
            recomputeOrderStatusFromItems(order);
            return;
        }


        if (isBuyer && newStatus == OrderStatus.DELIVERED) {
            boolean nonCancelledAllShipped = order.getOrderItems().stream()
                    .filter(i -> i.getStatus() != OrderItemStatus.CANCELLED)
                    .allMatch(i -> i.getStatus() == OrderItemStatus.SHIPPED || i.getStatus() == OrderItemStatus.DELIVERED);

            if (!nonCancelledAllShipped) {
                throw new IllegalStateException("Нельзя подтвердить весь заказ: имеются позиции, которые не отправлены или отменены");
            }

            List<OrderItem> shippedItems = order.getOrderItems().stream()
                    .filter(i -> i.getStatus() == OrderItemStatus.SHIPPED)
                    .toList();
            shippedItems.forEach(i -> i.setStatus(OrderItemStatus.DELIVERED));
            orderItemRepository.saveAll(shippedItems);

            recomputeOrderStatusFromItems(order);
            return;
        }

        throw new SecurityException("У вас нет прав для смены этого статуса");
    }

    @Transactional
    public void changeOrderItemStatus(Integer itemId, User currentUser, String newStatusStr) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));

        OrderItemStatus newStatus;
        try {
            newStatus = OrderItemStatus.valueOf(newStatusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный статус айтема");
        }

        Order order = item.getOrder();

        boolean isBuyer = order.getUser().getId().equals(currentUser.getId());
        boolean isSellerOfItem = item.getProduct().getSeller().getId().equals(currentUser.getId());

        if (isSellerOfItem) {
            if (newStatus == OrderItemStatus.CONFIRMED || newStatus == OrderItemStatus.CANCELLED) {
                if (item.getStatus() == OrderItemStatus.PROCESSING) {
                    item.setStatus(newStatus);
                    orderItemRepository.save(item);
                    recomputeOrderStatusFromItems(order);
                    return;
                } else {
                    throw new IllegalStateException("Нельзя изменить статус: текущий статус = " + item.getStatus());
                }
            }

            if (newStatus == OrderItemStatus.SHIPPED) {
                if (item.getStatus() == OrderItemStatus.CONFIRMED) {
                    item.setStatus(OrderItemStatus.SHIPPED);
                    orderItemRepository.save(item);
                    recomputeOrderStatusFromItems(order);
                    return;
                } else {
                    throw new IllegalStateException("Можно отправить только подтвержденный товар");
                }
            }

            throw new SecurityException("Неверная операция для продавца");
        }

        if (isBuyer) {
            if (newStatus == OrderItemStatus.DELIVERED) {
                if (item.getStatus() == OrderItemStatus.SHIPPED) {
                    item.setStatus(OrderItemStatus.DELIVERED);
                    orderItemRepository.save(item);
                    recomputeOrderStatusFromItems(order);
                    return;
                } else {
                    throw new IllegalStateException("Можно подтвердить только отправленный товар");
                }
            }
        }

        throw new SecurityException("У вас нет прав для смены этого статуса айтема");
    }


    @Transactional
    public void recomputeOrderStatusFromItems(Order order) {
        var freshOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItem> items = freshOrder.getOrderItems().stream().toList();

        if (items == null || items.isEmpty()) {
            freshOrder.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(freshOrder);
            return;
        }

        OrderStatus newStatus = calculateAggregateStatus(items);

        if (freshOrder.getStatus() != newStatus) {
            freshOrder.setStatus(newStatus);
            orderRepository.save(freshOrder);
        }
    }

    private OrderStatus calculateAggregateStatus(List<OrderItem> items) {
        List<OrderItemStatus> statuses = items.stream()
                .map(OrderItem::getStatus)
                .toList();

        boolean allCancelledOrRefunded = statuses.stream()
                .allMatch(s -> s == OrderItemStatus.CANCELLED || s == OrderItemStatus.REFUNDED);

        if (allCancelledOrRefunded) {
            boolean allRefunded = statuses.stream().allMatch(s -> s == OrderItemStatus.REFUNDED);
            return allRefunded ? OrderStatus.REFUNDED : OrderStatus.CANCELLED;
        }

        List<OrderItemStatus> activeStatuses = statuses.stream()
                .filter(s -> s != OrderItemStatus.CANCELLED && s != OrderItemStatus.REFUNDED)
                .toList();

        boolean allActiveDelivered = activeStatuses.stream()
                .allMatch(s -> s == OrderItemStatus.DELIVERED);

        if (allActiveDelivered) {
            return OrderStatus.DELIVERED;
        }

        boolean anyShippedOrDelivered = activeStatuses.stream()
                .anyMatch(s -> s == OrderItemStatus.SHIPPED || s == OrderItemStatus.DELIVERED);

        if (anyShippedOrDelivered) {
            return OrderStatus.SHIPPED;
        }

        boolean anyConfirmed = activeStatuses.stream()
                .anyMatch(s -> s == OrderItemStatus.CONFIRMED);

        if (anyConfirmed) {
            return OrderStatus.CONFIRMED;
        }

        return OrderStatus.PROCESSING;
    }

    public SellerStatsDto getSellerStats(Long sellerId, Pageable topProductsPageable) {
        BigDecimal revenue = orderItemRepository.sumRevenueBySellerId(sellerId);
        Long ordersCount = orderItemRepository.countDistinctOrdersBySellerId(sellerId);
        List<ProductStatDto> topProducts = orderItemRepository.findTopSellingProductsBySellerId(sellerId, topProductsPageable);

        SellerStatsDto dto = new SellerStatsDto();
        dto.setTotalRevenue(revenue == null ? BigDecimal.ZERO : revenue);
        dto.setOrdersCount(ordersCount == null ? 0L : ordersCount);
        dto.setTopProducts(topProducts);
        return dto;
    }
}