package org.steelboard.marketplace.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.steelboard.marketplace.dto.product.AddProductDto;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.exception.ProductNotFoundException;
import org.steelboard.marketplace.service.FileStorageService;
import org.steelboard.marketplace.service.ProductService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public String getProductPage(@PathVariable Long id, Model model) {

        Product product = productService.getProduct(id);

        // Покупателю показываем только активный товар
        if (!product.getActive()) {
            throw new ProductNotFoundException(id);
        }

        model.addAttribute("product", product);
        return "product"; // templates/product.html
    }

    @GetMapping("/new")
    public String addProduct(Model model) {
        model.addAttribute("productDto", new AddProductDto());
        return "add_product";
    }

    @PostMapping("/products/new")
    public String addProduct(@Valid @ModelAttribute AddProductDto productDto,
                             BindingResult bindingResult,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "add_product"; // возвращаем форму с ошибками
        }

        // Логика сохранения картинок и продукта, как раньше
        String mainImagePath = fileStorageService.saveFile(productDto.getMainImage(), "uploads/products");

        List<String> additionalImagePaths = new ArrayList<>();
        if (productDto.getAdditionalImages() != null) {
            for (MultipartFile file : productDto.getAdditionalImages()) {
                if (!file.isEmpty()) {
                    additionalImagePaths.add(fileStorageService.saveFile(file, "uploads/products"));
                }
            }
        }

        productService.createProduct(productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice(),
                mainImagePath,
                additionalImagePaths);

        return "redirect:/products/new?success";
    }
}
