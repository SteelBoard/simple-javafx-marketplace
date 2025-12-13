package org.steelboard.marketplace.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.exception.ProductNotFoundException;
import org.steelboard.marketplace.service.ProductService;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product/{id}")
    public String getProductPage(@PathVariable Long id, Model model) {

        Product product = productService.getProduct(id);

        // Покупателю показываем только активный товар
        if (!product.getActive()) {
            throw new ProductNotFoundException(id);
        }

        model.addAttribute("product", product);
        return "product"; // templates/product.html
    }
}
