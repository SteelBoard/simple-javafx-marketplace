package org.steelboard.marketplace.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.exception.OrderNotFoundException;
import org.steelboard.marketplace.repository.ReviewRepository;

@AllArgsConstructor
@Service
public class ReviewService {

    private ReviewRepository reviewRepository;

    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    public void updateReview(Long id, Integer rating, String comment) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);
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
