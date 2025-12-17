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
import org.steelboard.marketplace.service.ReviewService; // Предполагается наличие сервиса

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    // --- СПИСОК ОТЗЫВОВ ---
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String dir, // Отзывы обычно смотрят новые (desc)
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Маппинг полей для сортировки
        String sortField = mapSortField(sort);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // Сервис должен уметь искать по email юзера, названию товара или тексту отзыва
        Page<Review> pageResult = reviewService.findAll(search, pageable);

        model.addAttribute("page", pageResult);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);

        return "admin/review/reviews";
    }

    // --- ДЕТАЛИ / РЕДАКТИРОВАНИЕ ---
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Отзыв не найден"));
        model.addAttribute("review", review);
        return "admin/review/review_details";
    }

    // --- ОБНОВЛЕНИЕ ---
    @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @RequestParam("rating") Integer rating,
            @RequestParam("comment") String comment,
            Model model
    ) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Отзыв не найден"));

        // Валидация рейтинга
        if (rating == null || rating < 1 || rating > 5) {
            model.addAttribute("error", "Рейтинг должен быть от 1 до 5");
            // Возвращаем данные обратно в форму
            review.setRating(rating);
            review.setComment(comment);
            model.addAttribute("review", review);
            return "admin/review/review_details";
        }

        review.setRating(rating);
        review.setComment(comment);
        reviewRepository.save(review);

        return "redirect:/admin/reviews/" + id + "?success";
    }

    // --- УДАЛЕНИЕ (Опционально, для модерации часто нужно) ---
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }

    // Маппер полей сортировки
    private String mapSortField(String sort) {
        return switch (sort) {
            case "user" -> "user.email";
            case "product" -> "product.name"; // <-- ИСПРАВЛЕНО ЗДЕСЬ (было product.title)
            case "rating" -> "rating";
            case "date" -> "createdAt";
            default -> "id";
        };
    }
}