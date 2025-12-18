package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct_Id(Long productId, Pageable pageable);

    List<Review> findByProduct_Id(Long productId);

    List<Review> findByUser_Id(Long userId);

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN r.user u " +
            "LEFT JOIN r.product p " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " + 
            "LOWER(r.comment) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Review> search(@Param("search") String search, Pageable pageable);
}
