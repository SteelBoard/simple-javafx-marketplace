package org.steelboard.marketplace.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.entity.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct_Id(Long productId, Pageable pageable);

    List<Review> findByProduct_Id(Long productId);

    List<Review> findByUser_Id(Long userId);

    @Query("SELECT r FROM Review r WHERE " +
            "(:search IS NULL OR :search = '') OR " +
            "(LOWER(r.user.username) LIKE LOWER(CONCAT('%', :search, '%'))) OR " + // Поиск по Username
            "(LOWER(r.product.name) LIKE LOWER(CONCAT('%', :search, '%'))) OR " +
            "(LOWER(r.comment) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Review> search(@Param("search") String search, Pageable pageable);

    Page<Review> findByUser_Id(Long userId, Pageable pageable);

    Page<Review> searchByUser_IdAndCommentContainingIgnoreCase(Long userId, String comment, Pageable pageable);

    Long user(User user);
}
