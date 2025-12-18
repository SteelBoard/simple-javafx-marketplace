package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findById(Long id);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%')) " + 
            "OR " +
            "UPPER(o.status) LIKE UPPER(CONCAT('%', :search, '%'))")
    Page<Order> findBySearch(@Param("search") String search, Pageable pageable);

    List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId")
    Page<Order> findOrdersByProductId(@Param("productId") Long productId, Pageable pageable);

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

    @Query("SELECT o FROM Order o WHERE " +
            "(:search IS NULL OR :search = '') OR " +
            "(CAST(o.id AS string) LIKE %:search%) OR " +
            "(LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(LOWER(CAST(o.status AS string)) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Order> findAllBySearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.createdAt >= CURRENT_DATE")
    BigDecimal sumTotalToday();

    // 2. Количество заказов за сегодня
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= CURRENT_DATE")
    Long countOrdersToday();

    // 3. Данные для графика (Выручка по дням за последние 7 дней)
    @Query(value = "SELECT CAST(created_at AS DATE) as date, SUM(total_amount) as total " +
            "FROM orders WHERE created_at >= CURRENT_DATE - 7 " +
            "GROUP BY CAST(created_at AS DATE) ORDER BY date ASC", nativeQuery = true)
    List<Object[]> getRevenueLast7Days();

    // 4. Поиск заказов по ПВЗ (Исправление для вашей проблемы с ПВЗ)
    Page<Order> findByPickupPoint_Id(Long pickupPointId, Pageable pageable);

    boolean existsByPickupPoint_Id(Long pickupPointId);
}
