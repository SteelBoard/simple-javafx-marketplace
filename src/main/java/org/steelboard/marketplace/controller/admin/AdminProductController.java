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
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.service.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

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
            // Ищем по названию
            productsPage = productService.searchProductsByName(search, pageRequest);
        } else {
            productsPage = productService.getProducts(pageRequest);
        }

        model.addAttribute("productsPage", productsPage);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("size", size);
        model.addAttribute("search", search); // чтобы форма поиска заполнялась

        return "admin/product/products";
    }


    @GetMapping("/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Product product = productService.getProduct(id);
        ProductEditDto dto = productService.toEditDto(product);

        model.addAttribute("productDto", dto);
        model.addAttribute("product", product);
        return "admin/product/product_details";
    }

    /* Обновление основных данных */
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
        return "redirect:/admin/products/" + id;
    }

    /* Удаление картинки */
    @PostMapping("/images/delete")
    public String deleteImage(
            @RequestParam Long productId,
            @RequestParam Long imageId
    ) {
        productService.deleteImage(productId, imageId);
        return "redirect:/admin/products/" + productId;
    }
}

