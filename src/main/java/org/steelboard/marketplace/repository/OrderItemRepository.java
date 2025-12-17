package org.steelboard.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.OrderItem;
import org.steelboard.marketplace.entity.OrderStatus;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    boolean existsByOrder_User_IdAndProduct_IdAndOrder_Status(Long orderUserId, Long productId, OrderStatus orderStatus);
}
