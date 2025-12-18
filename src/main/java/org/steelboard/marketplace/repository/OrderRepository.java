package org.steelboard.marketplace.repository;

import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(Long id);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%')) " + 
            "OR " +
            "UPPER(o.status) LIKE UPPER(CONCAT('%', :search, '%'))")
    Page<Order> findBySearch(@Param("search") String search, Pageable pageable);

    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    Page<Order> findOrdersByProductId(@Param("productId") Long productId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.pickupPoint.id = :pvzId")
    Page<Order> findOrdersByPvzId(@Param("pvzId") Long pvzId, Pageable pageable);

    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    BigDecimal sumTotalRevenue();

    // 1. Все заказы пользователя
    Page<Order> findByUser_Id(Long userId, Pageable pageable);

    // 2. Поиск заказов пользователя по ID заказа (преобразуем ID в строку для поиска LIKE)
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND CAST(o.id AS string) LIKE %:search%")
    Page<Order> findByUserIdAndSearch(@Param("userId") Long userId,
                                      @Param("search") String search,
                                      Pageable pageable);

    Long countByCreatedAtAfter(Date date);
}
