package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.exception.OrderNotFoundException;
import org.steelboard.marketplace.repository.ReviewRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class ReviewService {

    private final ProductService productService;
    private final UserService userService;
    private ReviewRepository reviewRepository;

    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }

    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUser_Id(userId);
    }

    @Transactional
    public void addReview(User userDetails, Long productId, Integer rating, String comment) {
        Product product = productService.getProduct(productId);
        User user = userService.findById(userDetails.getId());

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);

        reviewRepository.save(review);
        productService.updateRating(productId, findByProductId(productId));
    }

    @Transactional(readOnly = true)
    public Page<Review> findAll(String search, Pageable pageable) {
        // Если строка поиска пустая, метод репозитория thanks to (:search IS NULL OR :search = '')
        // всё равно вернет все записи, но можно оптимизировать вызов:

        if (search == null || search.isBlank()) {
            return reviewRepository.findAll(pageable);
        }

        return reviewRepository.search(search.trim(), pageable);
    }

    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    public Page<Review> findByProductId(Long id, Pageable pageable) {
        return reviewRepository.findByProduct_Id(id, pageable);
    }

    public Page<Review> getReviews(Long productId, int page, String sort) {
        Sort pageSort;
        if (sort.equals("latest")) {
            pageSort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        else {
            pageSort = Sort.by(sort.equals("worst") ? Sort.Direction.ASC : Sort.Direction.DESC, "rating");
        }
        return reviewRepository.findByProduct_Id(
                productId,
                PageRequest.of(
                        page,
                        10,
                        pageSort
        ));
    }
}
