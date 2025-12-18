package org.steelboard.marketplace.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.steelboard.marketplace.dto.product.ProductEditDto;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.repository.ReviewRepository;
import org.steelboard.marketplace.service.ReviewService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    
    @GetMapping
    public String products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<Product> productsPage;

        if (search != null && !search.isBlank()) {
            productsPage = productService.searchProductsByName(search, pageRequest);
        } else {
            productsPage = productService.getProducts(pageRequest);
        }

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("search", search);

        return "admin/product/products";
    }

    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        try {
            Product product = productService.getProduct(id);
            if (!model.containsAttribute("productDto")) {
                model.addAttribute("productDto", productService.toEditDto(product));
            }
            model.addAttribute("product", product);
            return "admin/product/product_details";
        } catch (Exception e) {
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/{id}/update")
    public String updateProduct(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductEditDto dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            
            Product product = productService.getProduct(id);
            model.addAttribute("product", product); 
            return "admin/product/product_details";
        }
        productService.updateProductFromDto(id, dto);
        return "redirect:/admin/products/" + id + "?success";
    }

    

    @GetMapping("/{id}/reviews")
    public String productReviews(@PathVariable Long id, Model model) {
        Product product = productService.getProduct(id);
        List<Review> reviews = reviewService.findByProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        return "admin/product/product_reviews"; 
    }

    @PostMapping("/{productId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long productId, @PathVariable Long reviewId) {
        reviewService.deleteById(reviewId);
        return "redirect:/admin/products/" + productId + "/reviews";
    }

    

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/images/delete")
    public String deleteImage(@RequestParam Long productId, @RequestParam Long imageId) {
        productService.deleteImage(productId, imageId);
        return "redirect:/admin/products/" + productId;
    }

    @PostMapping("/{id}/images/add")
    public String addImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            productService.addImage(id, file);
        } catch (Exception e) {
            e.printStackTrace(); 
            
        }
        return "redirect:/admin/products/" + id;
    }

    @PostMapping("/images/set-main")
    public String setMainImage(
            @RequestParam Long productId,
            @RequestParam Long imageId
    ) {
        productService.setMainImage(productId, imageId);
        return "redirect:/admin/products/" + productId;
    }
}