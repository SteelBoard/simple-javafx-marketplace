package org.steelboard.marketplace.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.steelboard.marketplace.dto.product.ProductEditDto;
import org.steelboard.marketplace.entity.Order;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.Review;
import org.steelboard.marketplace.service.OrderService; // Нужно создать/добавить метод
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.service.ReviewService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final OrderService orderService; // Добавляем сервис заказов

    @GetMapping
    public String products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort, // Лучше по дате по умолчанию
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {
        Sort.Direction direction = dir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
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

    @PostMapping("/{sku}/update")
    public String updateProduct(
            @PathVariable String sku,
            @Valid @ModelAttribute("productDto") ProductEditDto dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productService.getProductBySku(sku);

        if (result.hasErrors()) {
            model.addAttribute("product", product);
            return "admin/product/product_details";
        }

        productService.updateProductFromDto(product.getId(), dto);
        redirectAttributes.addFlashAttribute("success", "Товар успешно обновлен!");
        return "redirect:/admin/products/" + sku;
    }

    @GetMapping("/{sku}/reviews")
    public String productReviews(@PathVariable String sku, Model model) {
        Product product = productService.getProductBySku(sku);
        List<Review> reviews = reviewService.findByProductId(product.getId());

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        return "admin/product/product_reviews";
    }

    // НОВЫЙ МЕТОД: Просмотр заказов с этим товаром
    @GetMapping("/{sku}/orders")
    public String productOrders(
            @PathVariable String sku,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Product product = productService.getProductBySku(sku);

        // В OrderService нужно добавить метод findOrdersByProductId(Long productId, Pageable pageable)
        // Если его нет, пока верните пустую страницу или добавьте метод в репозиторий
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> ordersPage = orderService.findOrdersByProductId(product.getId(), pageable);

        model.addAttribute("product", product);
        model.addAttribute("ordersPage", ordersPage);

        return "admin/product/product_orders";
    }

    @PostMapping("/{sku}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable String sku, @PathVariable Long reviewId) {
        reviewService.deleteById(reviewId);
        return "redirect:/admin/products/" + sku + "/reviews";
    }

    @PostMapping("/{sku}/delete")
    public String deleteProduct(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        productService.deleteProduct(product.getId());
        return "redirect:/admin/products";
    }

    @PostMapping("/{sku}/images/delete")
    public String deleteImage(
            @PathVariable String sku,
            @RequestParam Long imageId
    ) {
        Product product = productService.getProductBySku(sku);
        productService.deleteImage(product.getId(), imageId);
        return "redirect:/admin/products/" + sku;
    }

    @PostMapping("/{sku}/images/add")
    public String addImage(
            @PathVariable String sku,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        Product product = productService.getProductBySku(sku);

        // ВАЛИДАЦИЯ КОЛИЧЕСТВА ФОТО (Макс 11: 1 главная + 10 галерея)
        if (product.getImages().size() >= 11) {
            redirectAttributes.addFlashAttribute("error", "Достигнут лимит фотографий (максимум 11).");
            return "redirect:/admin/products/" + sku;
        }

        try {
            productService.addImage(product.getId(), file);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки файла.");
        }
        return "redirect:/admin/products/" + sku;
    }

    @PostMapping("/{sku}/images/set-main")
    public String setMainImage(
            @PathVariable String sku,
            @RequestParam Long imageId
    ) {
        Product product = productService.getProductBySku(sku);
        productService.setMainImage(product.getId(), imageId);
        return "redirect:/admin/products/" + sku;
    }
}