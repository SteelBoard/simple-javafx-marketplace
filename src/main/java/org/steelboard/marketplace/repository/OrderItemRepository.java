package org.steelboard.marketplace.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.dto.product.ProductStatDto;
import org.steelboard.marketplace.entity.OrderItem;
import org.steelboard.marketplace.entity.OrderStatus;

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

    @Query("SELECT new org.steelboard.marketplace.dto.product.ProductStatDto(" +
            "  oi.product.id, oi.product.name, SUM(oi.quantity), SUM(oi.unitPrice * oi.quantity) ) " +
            "FROM OrderItem oi WHERE oi.product.seller.id = :sellerId " +
            "GROUP BY oi.product.id, oi.product.name ORDER BY SUM(oi.unitPrice * oi.quantity) DESC")
    List<ProductStatDto> findTopSellingProductsBySellerId(Long sellerId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(oi.unitPrice * oi.quantity), 0) FROM OrderItem oi WHERE oi.product.seller.id = :sellerId")
    BigDecimal sumRevenueBySellerId(Long sellerId);

    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi WHERE oi.product.seller.id = :sellerId")
    Long countDistinctOrdersBySellerId(Long sellerId);

    Optional<OrderItem> findById(Integer id);
}
