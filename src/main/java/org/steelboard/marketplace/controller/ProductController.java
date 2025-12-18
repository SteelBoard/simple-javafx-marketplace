package org.steelboard.marketplace.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.dto.product.AddProductDto;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.service.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ReviewService reviewService;
    private final OrderService orderService;
    private final UserService userService;

    // ИЗМЕНЕНО: URL принимает {sku}
    @GetMapping("/{sku}")
    public String productPage(
            @PathVariable String sku,
            @RequestParam(defaultValue = "best") String sort,
            @RequestParam(required = false) String error,
            Model model
    ) {
        // 1. Ищем по SKU
        Product product = productService.getProductBySku(sku);

        Pageable pageable = PageRequest.of(0, 5, sort.equals("worst") ? Sort.by("rating").ascending() : Sort.by("rating").descending());

        // 2. Для отзывов используем ID, который вытащили из продукта
        Page<Review> reviews = reviewService.findByProductId(product.getId(), pageable);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews.getContent());

        return "product";
    }

    // ИЗМЕНЕНО: URL принимает {sku}
    @GetMapping("/{sku}/reviews")
    @ResponseBody
    public String getProductReviews(
            @PathVariable String sku,
            @RequestParam(defaultValue = "best") String sort) {

        Product product = productService.getProductBySku(sku);

        List<Review> reviews = reviewService.findByProductId(
                product.getId(),
                PageRequest.of(0, 5, Sort.by(sort.equals("worst") ? Sort.Direction.ASC : Sort.Direction.DESC, "rating")
                )).toList();

        StringBuilder sb = new StringBuilder();
        for (Review r : reviews) {
            sb.append("<div class=\"review\">")
                    .append("<div>")
                    .append("<strong>").append(r.getUser().getUsername()).append("</strong>")
                    .append(" ⭐ <span>").append(r.getRating()).append("</span>")
                    .append("</div>")
                    .append("<p>").append(r.getComment()).append("</p>")
                    .append("</div>");
        }

        return sb.toString();
    }

    // ИЗМЕНЕНО: URL принимает {sku}
    @GetMapping("/{sku}/reviews/all")
    public String allReviewsPage(
            @PathVariable String sku,
            @RequestParam(defaultValue = "0 ") int page,
            @RequestParam(defaultValue = "best") String sort,
            Model model) {

        Product product = productService.getProductBySku(sku);

        Page<Review> reviews = reviewService.getReviews(product.getId(), page, sort);
        model.addAttribute("reviews", reviews);
        model.addAttribute("product", product); // Передаем весь объект product, чтобы в шаблоне взять sku
        model.addAttribute("productId", product.getId()); // Оставляем на всякий случай
        model.addAttribute("currentSort", sort);
        return "reviews";
    }

    // ИЗМЕНЕНО: URL принимает {sku}
    @PostMapping("/{sku}/review")
    public String addReview(
            @PathVariable String sku,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productService.getProductBySku(sku);

        if (rating == null || comment == null || comment.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Заполните все поля");
            // Редирект на SKU
            return "redirect:/product/" + sku;
        }

        boolean bought = orderService.hasUserBoughtProduct(user.getId(), product.getId());
        if (!bought) {
            redirectAttributes.addFlashAttribute("error", "Вы можете оставить отзыв только после покупки товара.");
            return "redirect:/product/" + sku;
        }

        reviewService.addReview(user, product.getId(), rating, comment);

        return "redirect:/product/" + sku + "?sort=best";
    }

    @GetMapping("/new")
    public String addProduct(Model model) {
        model.addAttribute("productDto", new AddProductDto());
        return "add_product";
    }

    @PostMapping("/new")
    public String addProduct(@Valid @ModelAttribute("productDto") AddProductDto productDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal User user) {

        if (bindingResult.hasErrors()) {
            return "add_product";
        }

        String mainImagePath =
                fileStorageService.saveFile(productDto.getMainImage(), "products");

        List<String> additionalImagePaths = new ArrayList<>();
        if (productDto.getAdditionalImages() != null) {
            for (MultipartFile file : productDto.getAdditionalImages()) {
                if (!file.isEmpty()) {
                    additionalImagePaths.add(
                            fileStorageService.saveFile(file, "products")
                    );
                }
            }
        }

        Product product = productService.createProduct(productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                mainImagePath,
                additionalImagePaths,
                userService.findById(user.getId()));

        // ИЗМЕНЕНО: редирект на созданный товар по SKU, а не ID
        return "redirect:/product/" + product.getSku();
    }
}