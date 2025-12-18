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

    // ИЗМЕНЕНО: принимаем sku вместо id
    @GetMapping("/{sku}")
    public String productDetails(@PathVariable String sku, Model model) {
        try {
            Product product = productService.getProductBySku(sku);
            if (!model.containsAttribute("productDto")) {
                model.addAttribute("productDto", productService.toEditDto(product));
            }
            model.addAttribute("product", product);
            return "admin/product/product_details";
        } catch (Exception e) {
            return "redirect:/admin/products";
        }
    }

    // ИЗМЕНЕНО: URL теперь использует sku, но обновление происходит по ID (полученному из sku)
    @PostMapping("/{sku}/update")
    public String updateProduct(
            @PathVariable String sku,
            @Valid @ModelAttribute("productDto") ProductEditDto dto,
            BindingResult result,
            Model model
    ) {
        Product product = productService.getProductBySku(sku);

        if (result.hasErrors()) {
            model.addAttribute("product", product);
            return "admin/product/product_details";
        }

        // Используем ID найденного продукта для обновления
        productService.updateProductFromDto(product.getId(), dto);
        return "redirect:/admin/products/" + sku + "?success";
    }

    // ИЗМЕНЕНО: sku в URL
    @GetMapping("/{sku}/reviews")
    public String productReviews(@PathVariable String sku, Model model) {
        Product product = productService.getProductBySku(sku);
        // Сервис отзывов работает по ID, берем его у продукта
        List<Review> reviews = reviewService.findByProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        return "admin/product/product_reviews";
    }

    // ИЗМЕНЕНО: Удаление отзыва. URL: /admin/products/{sku}/reviews/{reviewId}/delete
    @PostMapping("/{sku}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable String sku, @PathVariable Long reviewId) {
        reviewService.deleteById(reviewId);
        return "redirect:/admin/products/" + sku + "/reviews";
    }

    // ИЗМЕНЕНО: Удаление продукта по SKU
    @PostMapping("/{sku}/delete")
    public String deleteProduct(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        productService.deleteProduct(product.getId());
        return "redirect:/admin/products";
    }

    // ИЗМЕНЕНО: Удаление картинки.
    // В форме на фронте лучше передавать sku как скрытое поле или использовать PathVariable
    // Но чтобы меньше ломать форму, оставим RequestParam, но найдем ID
    @PostMapping("/{sku}/images/delete")
    public String deleteImage(
            @PathVariable String sku,       // Получаем SKU из URL
            @RequestParam Long imageId      // Получаем ID картинки из формы
    ) {
        Product product = productService.getProductBySku(sku);
        productService.deleteImage(product.getId(), imageId);

        return "redirect:/admin/products/" + sku;
    }

    // ИЗМЕНЕНО: sku в URL
    @PostMapping("/{sku}/images/add")
    public String addImage(
            @PathVariable String sku,
            @RequestParam("file") MultipartFile file
    ) {
        Product product = productService.getProductBySku(sku);
        try {
            productService.addImage(product.getId(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/products/" + sku;
    }

    // Редирект на SKU после установки главной картинки
    @PostMapping("/{sku}/images/set-main")
    public String setMainImage(
            @PathVariable String sku,       // Получаем SKU из URL
            @RequestParam Long imageId      // Получаем ID картинки из формы
    ) {
        // Сначала ищем по SKU, чтобы получить ID товара
        Product product = productService.getProductBySku(sku);

        // Выполняем действие
        productService.setMainImage(product.getId(), imageId);

        // Возвращаемся на страницу товара по SKU
        return "redirect:/admin/products/" + sku;
    }
}