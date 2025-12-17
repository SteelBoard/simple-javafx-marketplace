package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(Long id);

    @Query("SELECT o FROM Order o WHERE " +
            "LOWER(o.user.username) LIKE LOWER(CONCAT('%', :search, '%')) " + // Исправлено здесь
            "OR " +
            "UPPER(o.status) LIKE UPPER(CONCAT('%', :search, '%'))")
    Page<Order> findBySearch(@Param("search") String search, Pageable pageable);
}
