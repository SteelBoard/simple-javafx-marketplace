package org.steelboard.marketplace.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.repository.ReviewRepository;
import org.steelboard.marketplace.service.ReviewService; 

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;


    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String dir, 
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        
        String sortField = mapSortField(sort);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        
        Page<Review> pageResult = reviewService.findAll(search, pageable);

        model.addAttribute("page", pageResult);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("activeTab", "reviews");
        model.addAttribute("activeTab", "reviews");

        return "admin/review/reviews";
    }

    
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Review review = reviewService.findById(id);
        model.addAttribute("review", review);
        return "admin/review/review_details";
    }

    
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            Model model
    ) {
        Review review = reviewService.findById(id);
        
        if (rating == null || rating < 1 || rating > 5) {
            model.addAttribute("error", "Рейтинг должен быть от 1 до 5");
            
            review.setRating(rating);
            review.setComment(comment);
            model.addAttribute("review", review);
            return "admin/review/review_details";
        }

        review.setRating(rating);
        review.setComment(comment);
        reviewService.update(review);

        return "redirect:/admin/reviews/" + id + "?success";
    }

    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reviewService.delete(id);
        return "redirect:/admin/reviews";
    }

    
    private String mapSortField(String sort) {
        return switch (sort) {
            case "user" -> "user.email";
            case "product" -> "product.name"; 
            case "rating" -> "rating";
            case "date" -> "createdAt";
            default -> "id";
        };
    }
}