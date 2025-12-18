package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.dto.product.ProductStatDto;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.OrderItem;
import org.steelboard.marketplace.entity.OrderStatus;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    boolean existsByOrder_User_IdAndProduct_IdAndOrder_Status(Long orderUserId, Long productId, OrderStatus orderStatus);
    @Query("SELECT new org.steelboard.marketplace.dto.product.ProductStatDto(" +
            "  oi.product.id, " +
            "  oi.product.name, " +
            "  SUM(oi.quantity), " +
            "  SUM(oi.unitPrice * oi.quantity) " +
            ") " +
            "FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY SUM(oi.unitPrice * oi.quantity) DESC")
    List<ProductStatDto> findTopSellingProducts(Pageable pageable);
}
